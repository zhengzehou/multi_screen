import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:multi_screen/multi_presentation.dart';
import 'package:multi_screen_example/main.dart';

class TestPage extends StatefulWidget {
  @override
  _TestPageState createState() => _TestPageState();
}

class _TestPageState extends State<TestPage> {


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Page2"),
      ),
      body: SafeArea(
        child: LayoutBuilder(builder: (BuildContext context, BoxConstraints constraints) {
          Orientation orientation = MediaQuery.of(context).orientation;
          print(orientation);
          print("constraints.maxHeight ${constraints.maxHeight}");
          print("constraints.maxWidth ${constraints.maxWidth}");
          print("screen size ${MediaQuery.of(context).size}");
          var radio = MediaQuery.of(context).devicePixelRatio;
          var mSize = MediaQuery.of(context).size;
          var w = mSize.width*radio;
          var h = mSize.height*radio;
          var topPadding = MediaQuery.of(context).padding.top;// 刘海
          var bottomPadding = MediaQuery.of(context).padding.bottom;
          var displayFeatures = MediaQuery.of(context).displayFeatures;
          print("radio ${radio} ");
          print("设备像素 ${w} * ${h}");
          print("刘海 ${topPadding}");
          print("下面导航${bottomPadding}");
          print("displayFeatures ${displayFeatures}");
          return TestMsg(params: {"deviceM":"设备像素 ${w} * ${h}","radio":"radio ${radio} ",
            "screenSize":"screen size ${MediaQuery.of(context).size}",
            "maxHeight":"constraints.maxHeight ${constraints.maxHeight}",
            "maxWidth":"constraints.maxHeight ${constraints.maxWidth}"
          });
        },),
      ),
    );
  }
}

class TestMsg extends StatefulWidget {
  TestMsg({Key? key,this.params=const {}}) : super(key: key);
  var params;
  @override
  State<TestMsg> createState() => _TestMsgState();
}

class _TestMsgState extends State<TestMsg> {
  MultiPresentation? presentation;
  var screeInfo;
  StreamController<dynamic> streamController = new StreamController<dynamic>();
  StreamSubscription? streamSubscription;

  @override
  void initState() {
    // TODO: implement initState
    presentation = MultiPresentation.instance();
    presentation?.init();

    presentation?.registerListener("page2", streamController);
    streamSubscription = streamController.stream.listen((event) {
      setState(() {
        print(json.encode(event['value']));
        screeInfo=event['value'];
      });
    });
    super.initState();
  }
  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    // 取消订阅，避免内存泄漏
    streamSubscription?.cancel();
    presentation?.unregisterListener("page2");
  }
  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.yellow,
      height: double.infinity,
      width: double.infinity,
      child: Text("${widget.params}   subcrip message ${screeInfo} ",
        textAlign: TextAlign.center,
        style: TextStyle(
        fontSize: 30,
        color: Colors.green,
      ),),
    );
  }
}
