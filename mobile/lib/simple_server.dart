//import 'package:flutter/material.dart';
import 'dart:io';

class Polvo {
  int tentaculos;
  Polvo({this.tentaculos = 6});
}

void main() {
  var p = Polvo(tentaculos: 9);
  print("hello world");
  HttpServer.bind("localhost", 8080).then((HttpServer server) {
    server.listen((HttpRequest req) {
      req.response.write("data pulled from server");
      req.response.close();
    });
  });
}
