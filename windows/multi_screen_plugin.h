#ifndef FLUTTER_PLUGIN_MULTI_SCREEN_PLUGIN_H_
#define FLUTTER_PLUGIN_MULTI_SCREEN_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace multi_screen {

class MultiScreenPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  MultiScreenPlugin();

  virtual ~MultiScreenPlugin();

  // Disallow copy and assign.
  MultiScreenPlugin(const MultiScreenPlugin&) = delete;
  MultiScreenPlugin& operator=(const MultiScreenPlugin&) = delete;

 private:
  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace multi_screen

#endif  // FLUTTER_PLUGIN_MULTI_SCREEN_PLUGIN_H_
