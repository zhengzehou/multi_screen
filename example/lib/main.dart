import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:multi_screen/multi_presentation.dart';

import 'TestPage.dart';
import 'TestPage2.dart';

void main() async{
  runApp(MyApp());
  MultiPresentation presentation = MultiPresentation.instance();
  presentation.init();
  var info = await presentation.getPlatformInfo();
  print(jsonEncode(info));
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      routes: <String, WidgetBuilder>{
        "index": (BuildContext context) => MainPage(),
        "TestPage": (BuildContext context) => TestPage(),
        "testPage2": (BuildContext context) => TestPage2(),
      },
      initialRoute: "index",
    );
  }
}

class MainPage extends StatefulWidget {
  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  MultiPresentation? presentation;
  String screeInfo = "";
  bool? res = false;
  StreamController<dynamic> streamController = new StreamController<dynamic>();
  var _nativeMsg;

  @override
  void initState() {
    // TODO: implement initState
    presentation = MultiPresentation.instance();
    presentation?.registerListener("page1", streamController);
    streamController.stream.listen((event) {
      print(event.toString());
      // 监听Native发送的消息
      _nativeMsg = event;
      setState((){

      });
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Center(
          child: Column(
            children: [
              Row(
                children: [
                  TextButton(
                    onPressed: () async {
                      Map<String, dynamic> res = await presentation!.getDisNum();
                      setState(() {
                        screeInfo = res.toString();
                      });
                    },
                    child: Text("屏幕信息"),
                  ),
                  TextButton(
                    onPressed: () {
                      // presentation!.sendMsgFromNative();
                      presentation?.subscribeMsg("page1", {"value": "test"});
                    },
                    child: Text("Native"),
                  ),
                  TextButton(
                      style: ButtonStyle(
                          foregroundColor:
                          MaterialStateProperty.resolveWith((states) {
                            return Colors.black;
                          }),
                          backgroundColor: MaterialStateProperty.all(Colors.red)),
                      onPressed: () async {
                        res = await presentation!.setContentView(1, "TestPage");
                        setState(() {});
                      },
                      child: Text("设置副屏:" + res.toString())),
                  // FlatButton(
                  //     onPressed: () => {presentation?.close()}, child: Text("关闭双屏")),
                  FlatButton(
                      onPressed: () async{
                        res = await presentation!.setContentView(1, "testPage2");
                      }, child: Text("切页面")),
                  FlatButton(
                      onPressed: () => {
                        presentation?.subscribeMsg("page2", {"value": "test"})
                      },
                      child: Text("发送消息"))
                ],
              ),
              Text(screeInfo),
              SizedBox(height: 20,),
              Text("${_nativeMsg} "),
            ],
          )),
    );
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    streamController.close();
    presentation?.dispose();

  }
}