#include "include/multi_screen/multi_screen_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "multi_screen_plugin.h"

void MultiScreenPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  multi_screen::MultiScreenPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
