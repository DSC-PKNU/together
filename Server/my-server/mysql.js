var mysql      = require('mysql');

var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '0000',
  database : 'together_db',
});
  
connection.connect();
  
connection.query('SELECT * FROM user', function (error, results, fields) {
    if (error) {
        console.log(error);
    }
    console.log(results);
});
  
connection.end();