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

let log4js = require('log4js');
log4js.configure({
    appenders: {wallapop: {type: 'file', filename: 'logs/wallapop.log'}},
    categories: {default: {appenders: ['wallapop'], level: 'trace'}}
});
let logger = log4js.getLogger('wallapop');
app.set('logger', logger);

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

// routerUsuarioToken
let routerUsuarioToken = express.Router();

routerUsuarioToken.use(function(req, res, next) {
    // obtener el token, vía headers (opcionalmente GET y/o POST).
    let token = req.headers['token'] || req.body.token || req.query.token;
    if (token != null) {
        // verificar el token
        jwt.verify(token, 'secreto', function(err, infoToken) {
            if (err || (Date.now()/1000 - infoToken.tiempo) > 240 ){
                res.status(403); // Forbidden
                res.json({
                    acceso : false,
                    error: 'Token invalido o caducado'
                });
                // También podríamos comprobar que intoToken.usuario existe
                return;

            } else {
                // dejamos correr la petición
                res.usuario = infoToken.usuario;
                next();
            }
        });

    } else {
        res.status(403); // Forbidden
        res.json({
            acceso : false,
            mensaje: 'No hay Token'
        });
    }
});


// Aplicar routerUsuarioToken
app.use('/api/oferta', routerUsuarioToken);


// routerUsuarioSession
var routerUsuarioSession = express.Router();

routerUsuarioSession.use(function(req, res, next) {
    console.log("routerUsuarioSession");
    let criterio;
    if ( req.session.usuario ) {
        // dejamos correr la petición
        next();
    } else {
        res.redirect("/identificarse");
    }
});

//Aplicar routerUsuarioSession
app.use("/oferta/*",routerUsuarioSession);
app.use("/usuario/*",routerUsuarioSession);
app.use("/admin",routerUsuarioSession);

//routerUsuarioAdmin
let routerUsuarioAdmin = express.Router();

routerUsuarioAdmin.use(function(req, res, next) {
    console.log("routerUsuarioAdmin");
    if (req.session.usuario !== undefined && req.session.usuario.rol === 'admin') {
        // dejamos correr la petición
        next();
    } else {
        res.redirect("/index");
    }
});
//Aplicar routerUsuarioAdmin
app.use("/usuario/*" ,routerUsuarioAdmin);
app.use("/resetdb", routerUsuarioAdmin);
app.use("/admin", routerUsuarioAdmin);


app.use(express.static('public'));

//Variables
app.set('port', 8081);
app.set('db', 'mongodb://admin:sdi@mywallapop-shard-00-00.p7wgg.mongodb.net:27017,mywallapop-shard-00-01.p7wgg.mongodb.net:27017,mywallapop-shard-00-02.p7wgg.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-bzzkck-shard-0&authSource=admin&retryWrites=true&w=majority');

app.set('clave','abcdefg');
app.set('crypto',crypto);

//Rutas/controladores por lógica
require("./routes/rusuarios.js")(app, swig, gestorBD);
require("./routes/rofertas.js")(app, swig, gestorBD);
require("./routes/radmin.js")(app, swig, gestorBD);
require("./routes/rapiofertas.js")(app, gestorBD);


app.get('/', function (req, res) {
    res.redirect('/index');
})


app.use( function (err, req, res, next){
    console.log("Error producido: " +err); //mostramos el error por consola
    if (!res.headersSent){
        res.status(400);
        res.send("Recurso no disponible");
    }
});

//Lanzar el servidor
https.createServer({
    key: fs.readFileSync('certificates/alice.key'),
    cert: fs.readFileSync('certificates/alice.crt')
}, app).listen(app.get('port'), function() {
    console.log("Servidor activo");
});