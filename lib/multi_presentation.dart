import 'dart:async';
import 'dart:collection';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MultiPresentation {
  static MultiPresentation? _instance;

  MultiPresentation._internal();

  factory MultiPresentation.instance() => _getInstance()!;

  //flutter端推送到原生消息通道
  static const MethodChannel _channel = const MethodChannel('multi_screen');

  //原生主动推送消息通道
  static const EventChannel _eventChannel = const EventChannel("presentationEventChannel");

  static Stream<dynamic> _streamController = new StreamController<dynamic>().stream;

  Map<String, StreamController<dynamic>> _dispatchMap = new HashMap<String, StreamController<dynamic>>();

  //单例初始化获取Android端DisplayManager
  static MultiPresentation? _getInstance() {
    if (_instance == null) {
      print("create instance ${DateTime.now()}");
      _instance = MultiPresentation._internal();
    }
    return _instance;
  }
  static StreamController commonController = StreamController();

  init() {
    _channel.invokeMethod("init");
    _eventChannel.receiveBroadcastStream().listen((event) {
      try {
        // 广播消息，此处会收到所有后端返回的消息
        // 根据event中返回的方法值，判断是否有注册监听，如果有则添加返回值到注册的StreamController
        _dispatchMap[event["method"]]!.sink.add(event);
      } catch (e) {
        commonController.sink.add(event);
      }
    });
  }

  Future<Map<String, dynamic>> getDisNum() async {
    var result = await _channel.invokeMethod("getDisNum");
    return Map.from(result);
  }
  Future<Map<String, dynamic>> getPlatformInfo() async {
    var result = await _channel.invokeMethod("getPlatformInfo");
    return Map.from(result);
  }
  Future<String> getPlatformVersion() async {
    var result = await _channel.invokeMethod("getPlatformVersion");
    return result;
  }
  Future<String> getMACAddress() async {
    var result = await _channel.invokeMethod("getMACAddress");
    return result;
  }
  Future<String> getIpAddress() async {
    var result = await _channel.invokeMethod("getIpAddress");
    return result;
  }

  Stream<dynamic> get resPonseMessage => _streamController;

  Future<bool?> setContentView(int index, String rout) async {
    return await _channel.invokeMethod(
        "setContentView", <String, dynamic>{"index": index, "rout": rout});
  }

  close() {
    _channel.invokeMethod("close");
  }

  void dispose() {
    _dispatchMap.clear();
  }

  // 接收不同的监听，通过method进行存储
  void registerListener(String method, StreamController<dynamic> listeners) {
    try {
      if (!_dispatchMap.containsValue(listeners)) {
        _dispatchMap[method] = listeners;
      }
    } catch (e) {
      throw new Exception(e);
    }
  }

  void unregisterListener(String method) {
    if (_dispatchMap.containsKey(method)) {
      _dispatchMap.removeWhere((key, value) => key == method);
    }
  }

  void subscribeMsg(String method, dynamic value) {
    _channel.invokeMethod(
        "subscribeMsg", <String, dynamic>{"method": method, "value": value});
  }
}