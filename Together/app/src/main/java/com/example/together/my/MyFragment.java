package com.example.together.my;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.together.EmergencyCallSetting;
import com.example.together.login.LoginActivity;
import com.example.together.R;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.user.UserApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;


public class MyFragment extends Fragment {

    ArrayList<String> myListItem;
    ArrayList<String> logoutListItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        SharedPreferences pref = this.requireActivity()
                .getSharedPreferences("myInfo", Context.MODE_PRIVATE);

        TextView userName = view.findViewById(R.id.userName);
        TextView userEmail = view.findViewById(R.id.userEmail);

        ListView lvMy = view.findViewById(R.id.lvMy);
        ListView lvLogout = view.findViewById(R.id.lvLogout);

        initListItem();
        final MyAdapter myAdapter = new MyAdapter(this.getContext(), myListItem);
        final LogoutAdapter logoutAdapter = new LogoutAdapter(this.getContext(), logoutListItem);

        lvMy.setAdapter(myAdapter);
        lvLogout.setAdapter(logoutAdapter);

        lvMy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                Toast.makeText(getContext(), myAdapter.getItem(position).toString(),
                        Toast.LENGTH_SHORT).show();

                switch (position) {
                    case 0: // 긴급 전화번호 관리
                        Toast.makeText(getContext(), "긴급전화번호 관리 페이지로 넘어갑니다",
                                Toast.LENGTH_LONG).show();
                        Intent callSettingIntent = new Intent(getActivity(), EmergencyCallSetting.class);
                        startActivity(callSettingIntent);

                        break;

                    case 1: // 보호자 관리
                        break;

                    default: // 피보호자 관리
                }
            }
        });

        lvLogout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogoutDialogFragment dialog = new LogoutDialogFragment();
                assert getFragmentManager() != null;
//                dialog.show(getFragmentManager(), "로그아웃");
                // TODO: dialog 오류 해결

                UserApiClient.getInstance().unlink((error) -> {
                    if (error != null) {
                        Log.e(TAG, "로그아웃(연결 끊기) 실패.", error);
                    } else {
                        Log.i(TAG, "로그아웃(연결 끊기) 성공. SDK에서 토큰 삭제됨");

                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove("token");
                        editor.apply();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);

                        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    return null;
                });
            }
        });

        // 사용자 정보 요청
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error);
            }
            else if (user != null) {
                if (user.getKakaoAccount().getEmail() != null) {
                    Log.i(TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: "+ user.getId() +
                            "\n이메일: " + user.getKakaoAccount().getEmail() +
                            "\n닉네임: " + user.getKakaoAccount().getProfile().getNickname());

                    userName.setText(user.getKakaoAccount().getProfile().getNickname());
                    userEmail.setText(user.getKakaoAccount().getEmail());
                } else if (user.getKakaoAccount().getEmailNeedsAgreement() == false) {
                    Log.e(TAG, "사용자 계정에 이메일 없음. 꼭 필요하다면 동의항목 설정에서 수집 기능을 활성화 해보세요.");
                } else if (user.getKakaoAccount().getEmailNeedsAgreement() == true) {
                    Log.d(TAG, "사용자에게 이메일 제공 동의를 받아야 합니다.");

                    // 사용자에게 이메일 제공 동의 요청
                    List<String> scopes = Arrays.asList("account_email");
                    LoginClient.getInstance().loginWithNewScopes(this.getContext(), scopes, (token, emailError) -> {
                                if (emailError != null) {
                                    Log.e(TAG, "이메일 제공 동의 실패", emailError);
                                } else {
                                    Log.d(TAG, "allowed scopes: " + token.getScopes());

                                    // 사용자 정보 재요청
                                    UserApiClient.getInstance().me((emailUser, emailError2) -> {
                                                if (emailError2 != null) {
                                                    Log.e(TAG, "사용자 정보 요청 실패", emailError2);
                                                } else if (emailUser != null) {
                                                    Log.i(TAG, "이메일: " + emailUser.getKakaoAccount().getEmail());
                                                    userEmail.setText(emailUser.getKakaoAccount().getEmail());
                                                    userName.setText(emailUser.getKakaoAccount().getProfile().getNickname());

                                                    // TODO: Room 사용해서 UI가 데이터 변화를 감지하도록 변경
                                                }
                                                return null;
                                            });
                                }
                                return null;
                            });

                }

            }
            return null;
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void initListItem() {
        myListItem = new ArrayList<String>();
        logoutListItem = new ArrayList<String>();

        myListItem.add("긴급 전화번호 관리");
        myListItem.add("보호자 관리");
        myListItem.add("피보호자 관리");
        logoutListItem.add("로그아웃");
    }
}