var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');

var app = express();
var socketio = require('socket.io');

var server = app.listen(3001, () => {
    console.log('Listening at port number 3001')
})

// return socket.io server.
var io = socketio.listen(server)

var whoIsOn = [];

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);

// catch 404 and forward to error handler
app.use(function (req, res, next) {
    next(createError(404));
});

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

io.on('connection', function (socket) {
    var nickname = ''

    // 'login' 이벤트를 발생시킨 경우
    socket.on('login', function (data) {
        console.log(`${data} 입장 ------------------------`)
        whoIsOn.push(data)
        nickname = data

        var whoIsOnJson = `${whoIsOn}`
        console.log(whoIsOnJson)

        // 서버에 연결된 모든 소켓에 보냄
        io.emit('newUser', whoIsOnJson)
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
