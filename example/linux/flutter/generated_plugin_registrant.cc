//
//  Generated file. Do not edit.
//

// clang-format off

#include "generated_plugin_registrant.h"

#include <multi_screen/multi_screen_plugin.h>

void fl_register_plugins(FlPluginRegistry* registry) {
  g_autoptr(FlPluginRegistrar) multi_screen_registrar =
      fl_plugin_registry_get_registrar_for_plugin(registry, "MultiScreenPlugin");
  multi_screen_plugin_register_with_registrar(multi_screen_registrar);
}
