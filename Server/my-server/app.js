var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var mysql = require('mysql');
var db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '0000',
    database: 'together_db',
});
db.connect();

var app = express();
var socketio = require('socket.io');
const { stat } = require('fs');

var server = app.listen(3001, () => {
    console.log('Listening at port number 3001')
})

// return socket.io server.
var io = socketio.listen(server)

var whoIsOn = [];

// error handler
app.use(function (err, req, res, next) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.render('error');
});

module.exports = app;


app.get('/users', (req, res) => {
    console.log('who get in here /users');
    res.json("get some data")
});

app.post('/dup_check', (req, res) => {
    console.log('who get in here post /dup_check');
    var inputData;
    var user_id;
    var status = false;

    req.on('data', (data) => {
        inputData = JSON.parse(data);
    });
    req.on('end', () => {
        user_id = inputData.user_id
        console.log("user_id: " + user_id);

        db.query(`SELECT id FROM user WHERE id=?`, [user_id], function(error, results) {
            console.log(results)
            if (results.length == 0) {
                console.log("results의 길이는 0");
                status = true;
                
                res.write("1");
                console.log("send ok sign");
                res.end();
            } else {
                res.write("-1");
                console.log("send dup sign");
                res.end();
            }
        });
    });
})

app.post('/join', (req, res) => {
    console.log('who get in here post /join');
    var inputData;
    var user_id, user_pw, user_name
    req.on('data', (data) => {
        inputData = JSON.parse(data);
    });
    req.on('end', () => {
        user_id = inputData.user_id
        user_pw = inputData.user_pw
        user_name = inputData.name
        console.log("user_id: " + user_id + ", user_pw: " + user_pw + ", name: " + user_name);

        db.query(`SELECT id FROM user WHERE id=?`, [user_id], function(error, results) {
            console.log(results)
            if (results == null) {
                console.log("results는 null")
            }
        });

        db.query(`INSERT INTO user (id, password, name) VALUES(?, ?, ?)`, [user_id, user_pw, user_name], function(error, results) {
            console.log(results);
        });

        db.query(`SELECT * FROM user`, function(error, results) {
            console.log(results);
        });
    });
    
    res.write("OK!");
    res.end();
})

app.post('/login', (req, res) => {
    console.log('who get in here post /login');
    var inputData;
    var user_id, user_pw
    req.on('data', (data) => {
        inputData = JSON.parse(data);
    });
    req.on('end', () => {
        user_id = inputData.user_id
        user_pw = inputData.user_pw
        user_name = inputData.name
        console.log("user_id: " + user_id + ", user_pw: " + user_pw);

        db.query(`SELECT * FROM user WHERE id=? AND password=?`, [user_id, user_pw], function(error, results) {
            console.log(results);

            if (results.length == 0) {
                res.write("-1");
                res.end();
            } else {
                res.write("1");
                res.end();
            }
        });
    });
    
    
})

app.post('/cur_user', (req, res) => {
    console.log('who get in here post /cur_user');
    var inputData;
    var user_id
    req.on('data', (data) => {
        inputData = JSON.parse(data);
    });
    req.on('end', () => {
        user_id = inputData.user_id
        console.log("user_id: " + user_id);

        db.query(`SELECT * FROM user WHERE id=?`, [user_id], function(error, results) {
            console.log(results);

            res.write(results[0].name);
            res.end();
        });
    });
})


io.on('connection', function (socket) {
    var nickname = ''

    // 'login' 이벤트를 발생시킨 경우
    socket.on('login', function (data) {
        console.log(`${data} 입장 ------------------------`)
        whoIsOn.push(data)
        nickname = data

        var whoIsOnJson = `${whoIsOn}`
        console.log(whoIsOnJson)

        db.query(`SELECT * FROM user`, function(error, results) {
            console.log(results);
            
            
            io.emit('newUser', results)
        });

        // 서버에 연결된 모든 소켓에 보냄
        // io.emit('newUser', whoIsOnJson)
    })

    socket.on('say', function (data) {
        console.log(`${nickname} : ${data}`)

        socket.emit('myMsg', data)
        socket.broadcast.emit('newMsg', data)  // 현재 소켓 이외의 소켓에 보냄
    })

    socket.on('disconnect', function() {
        console.log(`${nickname} 퇴장 -----------------------`)
    })

    socket.on('logout', function() {
        whoIsOn.splice(whoIsOn.indexOf(nickname), 1)
        var data = {
            whoIsOn: whoIsOn,
            disconnected: nickname
        }
        socket.emit('logout', data)
    })
})
