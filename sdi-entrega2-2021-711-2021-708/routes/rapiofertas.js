module.exports = function (app, gestorBD) {

    //AUTENTICACION
    app.post("/api/autenticar/", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');

        let criterio = {
            email: req.body.email,
            password: seguro
        }

        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                res.status(401); //unauthorized
                res.json({
                    autenticado: false
                })
            } else {
                let token = app.get('jwt').sign(
                    {usuario: criterio.email, tiempo: Date.now() / 1000},
                    "secreto");

                res.status(200);
                res.json({
                    autenticado: true,
                    token: token
                })
            }
        });
    });

    //MOSTRAR EL LISTADO DE OFERTAS DISPONIBLES
    app.get("/api/oferta", function (req, res) {
        let token = req.headers['token'] || req.body.token || req.query.token;
        app.get('jwt').verify(token, 'secreto', function (err, infoToken) {
            if (err) {
                app.get("logger").error('API: Token no valido');
                res.status(403); // Forbidden
                res.json({
                    acceso: false,
                    error: 'Token invalido o caducado'
                });
            } else {
                // dejamos correr la petición
                let usuario = infoToken.usuario;
                let cri = {
                    autor: {$ne: res.usuario},
                    estado: {$ne: 'no disponible'}
                };
                gestorBD.obtenerOfertas(cri, function (ofertas) {
                    if (ofertas == null || ofertas.length === 0 || ofertas === undefined) {
                        app.get("logger").error('No se puede obtener la oferta');
                        res.status(204); // Unauthorized
                        res.json({
                            err: "No hay resultados"
                        });
                    } else {
                        app.get("logger").info('API: Mostradas las ofertas disponibles ');
                        res.status(200);
                        res.send(ofertas);
                    }
                })
            }
        })
    });


    // ENVIAR MENSAJE A UNA OFERTA
    app.post('/api/oferta/mensaje/:id', function (req, res) {
        let token = req.headers['token'] || req.body.token || req.query.token;
        app.get('jwt').verify(token, 'secreto', function (err, infoToken) {
                if (err) {
                    app.get("logger").error('API: Token no valido');
                    res.status(403); // Forbidden
                    res.json({
                        acceso: false,
                        error: 'Token invalido o caducado'
                    });
                } else {
                    let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};
                    gestorBD.obtenerOfertas(criterio, function (ofertas) {
                        if (ofertas == null || ofertas.length === 0) {
                            app.get("logger").info('API: Error al obtener ofertas');
                            res.status(403);
                            res.json({err: "No hay resultados"});
                        } else {
                            let usuario = infoToken.usuario;
                            let oferta = ofertas[0];
                            let criterio = {
                                $or: [
                                    {
                                        user1: usuario,
                                        offer: oferta._id
                                    },
                                    {
                                        user1: oferta.autor,
                                        offer: oferta._id
                                    },
                                    {
                                        user2: usuario,
                                        offer: oferta._id
                                    },
                                    {
                                        user2: oferta.autor,
                                        offer: oferta._id
                                    }
                                ]
                            };
                            //Obtenemos las conversaciones
                            gestorBD.obtenerConversaciones(criterio, function (conversacion) {
                                    if (conversacion == null || conversacion.length === 0) {
                                        var nuevaConver = {
                                            offer: gestorBD.mongo.ObjectID(oferta._id),
                                            title: oferta.nombre,
                                            user1: usuario,
                                            user2: oferta.autor,
                                            valid: true
                                        };
                                        gestorBD.crearNuevaConversacion(nuevaConver, function (conversacionNueva) {
                                            if (conversacionNueva === null) {
                                                app.get("logger").info('API: Error al crear la conversacion');
                                                res.status(204);
                                                res.json({error: "No ha sido posible crear la conversación"});
                                            } else {
                                                enviarMensaje(
                                                    req.body.message,
                                                    usuario,
                                                    oferta.autor,
                                                    conversacionNueva,
                                                    oferta._id,
                                                    req,
                                                    res);
                                            }
                                        });
                                    } else {
                                        enviarMensaje(
                                            req.body.message,
                                            usuario,
                                            req.body.receiver,
                                            conversacion[0]._id,
                                            oferta._id,
                                            req, res);
                                    }
                                }
                            );
                        }
                    });
                }
            }
        );
    });

    function enviarMensaje(text, sender, receiver, convId, offerId, req, res) {
        let message = {
            sender: sender,
            receiver: receiver,
            offer: offerId,
            message: text,
            fecha: new Date().toDateString(),
            read: false,
            idConversacion: convId
        };
        gestorBD.insertarMensaje(message, function (mensaje) {
            if (mensaje == null) {
                res.status(500); // error del servidor
                app.get("logger").info('API: Error al insertar el mensaje');
                res.json({
                    err: "Error del servidor"
                });
            } else {
                res.status(200);
                app.get("logger").info('API: Se ha insertado el mensaje');
                res.json(mensaje);
            }
        });

    }

    // carga conversacion y sus mensajes
    app.get("/api/oferta/conversacion/:id", function (req, res) {
        let crit = {"_id": gestorBD.mongo.ObjectID(req.params.id)};
        gestorBD.obtenerConversaciones(crit, function (conversaciones) {
            if (conversaciones == null) {
                res.status(500);
                app.get("logger").info('API: Error al obtener la conversacion');
                res.json({error: "Se ha producido un error"});
            } else if (conversaciones.length === 0) {
                res.status(200);
                res.json([]);
            } else {
                let conversacion = conversaciones[0];
                let criterio = {
                    idConversacion: gestorBD.mongo.ObjectID(conversacion._id)
                };
                gestorBD.obtenerMensajes(criterio, function (mensajes) {
                    if (mensajes == null) {
                        res.status(200);
                        app.get("logger").info('API: Error al obtener el mensaje');
                        res.json({
                            error: "Ha habido un error"
                        })
                    } else {
                        res.status(200);
                        app.get("logger").info('API: Mensaje obtenido correctamente');
                        res.json(mensajes);
                    }
                });
            }
        });
    });


    app.post("/api/mensaje/eliminar/", function (req, res) {
        let token = req.headers['token'] || req.body.token || req.query.token;
        app.get('jwt').verify(token, 'secreto', function (err, infoToken) {
            if (err) {
                res.status(403); // Forbidden
                app.get("logger").info('API: Token no valido');
                res.json({
                    acceso: false,
                    error: 'Token invalido o caducado'
                });
            } else {
                let crit = {"_id": gestorBD.mongo.ObjectID(req.body.id)};
                gestorBD.obtenerOfertas(crit, function (ofertas) {
                    if (ofertas == null) {
                        res.status(500);
                        app.get("logger").info('API: Error al obtener la oferta');
                        res.json({
                            error: "se ha producido un error"
                        })
                    } else if (ofertas.length === 0) {
                        res.status(400);
                        app.get("logger").info('API: Error, no se encuentra la oferta');
                        res.json({
                            error: "Oferta no encontrada"
                        })
                    } else {
                        let autor = req.body.receiver;
                        let user = infoToken.usuario;
                        let criterio = {
                            $or: [
                                {
                                    sender: user,
                                    receiver: autor,
                                    offer: gestorBD.mongo.ObjectID(req.body.id)
                                },
                                {
                                    sender: autor,
                                    receiver: user,
                                    offer: gestorBD.mongo.ObjectID(req.body.id)
                                }
                            ]
                        };
                        gestorBD.eliminarMensajes(criterio, function (mensajes) {
                            if (mensajes == null) {
                                res.status(500);
                                app.get("logger").info('API: Error al eliminar el mensaje');
                                res.json({
                                    error: "se ha producido un error"
                                })
                            } else {
                                res.status(200);
                                app.get("logger").info('API: Se ha eliminado correctamente el mensaje');
                                res.send(mensajes);
                            }
                        });
                    }
                });
            }

        });

    });

    app.get("/api/mensaje/leido/:id", function (req, res) {
        let criterio = {
            "_id": gestorBD.mongo.ObjectID(req.params.id)
        }
        gestorBD.marcarLeido(criterio, function (mensajes) {
            if (mensajes == null) {
                res.status(500);


                res.json({
                    error: "se ha producido un error"
                })
            } else {
                res.status(200);
                res.send(mensajes);
            }
        })
    });

    app.post("/api/oferta/conversacion/list", function (req, res) {
        let token = req.headers['token'] || req.body.token || req.query.token;
        app.get('jwt').verify(token, 'secreto', function (err, infoToken) {
            if (err) {
                res.status(403); // Forbidden
                app.get("logger").info('API: Token no valido');
                res.json({
                    acceso: false,
                    error: 'Token invalido o caducado'
                });
            } else {
                let criterio = {$or: [{user1: infoToken.usuario}, {user2: infoToken.usuario}]};
                gestorBD.obtenerConversaciones(criterio, function (conver) {
                    if (conver == null) {
                        res.status(500)
                        app.get("logger").info('API: Error mostrando las conversaciones');
                        res.json({
                            error: "Se ha producido un error"
                        })
                    } else {
                        app.get("logger").info('API: Se han listado las conversaciones');
                        res.send(conver);

                    }
                });
            }

        });
    });


    app.get("/api/conversacion/borrar/:id", function (req, res) {
        let idOferta = gestorBD.mongo.ObjectID(req.params.id);
        let criterio = {idConversacion: idOferta};

        gestorBD.obtenerMensajes(criterio, function (mensajes) {
            if (mensajes == null) {
                res.status(500);
                app.get("logger").info('API: Error mostrando los mensajes');
                res.json({
                    error: "se ha producido un error"
                })
            } else if (mensajes.length === 0) {
                res.status(400);
                app.get("logger").info('API: Error mostrando los mensajes');
                res.json({
                    error: "NO se ha encontrado la oferta"
                })
            } else {
                gestorBD.eliminarMensajes(criterio, function (mensajes) {
                    if (mensajes == null) {
                        res.status(500);
                        app.get("logger").info('API: Error eliminando los mensajes');
                        res.json({
                            error: "se ha producido un error"
                        })
                    } else {
                        let criterioConversacion = {_id: idOferta};
                        gestorBD.eliminarConversaciones(criterioConversacion, function (mensajes) {
                            if (mensajes == null) {
                                res.status(500);
                                app.get("logger").info('API: Error eliminando los mensajes');
                                res.json({
                                    error: "se ha producido un error"
                                })
                            } else {
                                res.status(200);
                                app.get("logger").info('API: Se han eliminado los mensajes');
                                res.send((mensajes));
                            }
                        });
                    }
                });
            }
        });

    });

    app.post('/api/buscar/oferta/conversacion/:id', function (req, res) {
        let token = req.headers['token'] || req.body.token || req.query.token;
        app.get('jwt').verify(token, 'secreto', function (err, infoToken) {
            if (err) {
                res.status(403); // Forbidden
                res.json({
                    acceso: false,
                    error: 'Token invalido o caducado'
                });
            } else {
                let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};
                gestorBD.obtenerOfertas(criterio, function (ofertas) {
                    if (ofertas == null || ofertas.length === 0) {
                        res.status(403);
                        app.get("logger").info('API: Error al obtener la oferta');
                        res.json({
                            acceso: false,
                            error: 'Token invalido o caducado'
                        });
                    } else {
                        let usuario = infoToken.usuario;
                        let oferta = ofertas[0];
                        let criterio = {
                            $or: [
                                {
                                    user1: usuario,
                                    offer: oferta._id
                                },
                                {
                                    user1: oferta.autor,
                                    offer: oferta._id
                                }
                            ]
                        };
                        gestorBD.obtenerConversaciones(criterio, function (conversacion) {

                            if (conversacion == null) {
                                res.status(500);
                                app.get("logger").info('API: Conversacion no encontrada');
                                res.json({error: "Se ha producido un error"});
                            } else if (conversacion.length === 0) {
                                var nuevaConver = {
                                    offer: gestorBD.mongo.ObjectID(oferta._id),
                                    title: oferta.nombre,
                                    user1: oferta.autor,
                                    user2: usuario,
                                    valid: true
                                };
                                gestorBD.crearNuevaConversacion(nuevaConver, function (conversacionNueva) {
                                    if (conversacionNueva === null) {
                                        res.status(204);
                                        app.get("logger").info('API: Error al crear la conversacion');
                                        res.json({
                                            error: "No fue posible crear la conversación"
                                        });
                                    } else {
                                        enviarMensaje(
                                            'Hola! Me interesa su oferta. ',
                                            usuario,
                                            oferta.autor,
                                            conversacionNueva,
                                            oferta._id,
                                            req, res);
                                    }
                                })
                            } else {
                                app.get("logger").info('API: Se han listado las conversaciones');
                                res.send(conversacion[0]);
                            }
                        });
                    }
                });
            }
        })
    });

}
