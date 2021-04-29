//Módulos
let express = require('express');
let app = express();

let rest = require('request');
app.set('rest',rest);

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Credentials", "true");
    res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
    // Debemos especificar todas las headers que se aceptan. Content-Type , token
    next();
});

let jwt = require('jsonwebtoken');
app.set('jwt',jwt);

let fs = require('fs');
let https = require('https');

let crypto = require('crypto'); //contraseñas

let expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));

let fileUpload = require('express-fileupload');
app.use(fileUpload());
//base de datos mongo
let mongo = require('mongodb');
let swig = require('swig');
let bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

let gestorBD = require("./modules/gestorBD.js"); gestorBD.init(app,mongo);
gestorBD.init(app, mongo);

//Rutas/controladores por lógica
require("./routes/rusuarios.js")(app, swig, gestorBD);

//Variables
app.set('port', 8081);
app.set('db','mongodb://admin:sdi@mywallapop-shard-00-00.p7wgg.mongodb.net:27017,mywallapop-shard-00-01.p7wgg.mongodb.net:27017,mywallapop-shard-00-02.p7wgg.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-bzzkck-shard-0&authSource=admin&retryWrites=true&w=majority');
app.set('clave','abcdefg');
app.set('crypto',crypto);

//lanzar el servidor
app.listen(app.get('port'), function(){
    console.log("Servidor activo");
})