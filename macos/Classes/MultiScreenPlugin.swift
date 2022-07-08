import Cocoa
import FlutterMacOS

public class MultiScreenPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "multi_screen", binaryMessenger: registrar.messenger)
    let instance = MultiScreenPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getPlatformVersion":
      result("macOS " + ProcessInfo.processInfo.operatingSystemVersionString)
  case "getPlatformInfo":
        result("macOS " + ProcessInfo.processInfo)
    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
