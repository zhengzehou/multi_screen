package top.betteryou.multi_screen.screen;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import io.flutter.FlutterInjector;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import top.betteryou.multi_screen.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public abstract class DifferentDisplay extends Presentation {

    protected View layoutView;
    private Context mContext;
    private FlutterEngine flutterEngine;

    public DifferentDisplay(Context outerContext, Display display) {
        super(outerContext, display);
        mContext = outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutView = this.getLayoutView();
        if (layoutView == null) {
            flutterEngine = new FlutterEngine(mContext);
            flutterEngine.getNavigationChannel().setInitialRoute(this.viewRout());
            flutterEngine.getDartExecutor().executeDartEntrypoint(new DartExecutor.DartEntrypoint(FlutterInjector.instance().flutterLoader().findAppBundlePath(), "main"));
            setContentView(R.layout.flutter_presentation_view);
            FlutterView view = findViewById(R.id.flutter_presentation_view);
            view.attachToFlutterEngine(flutterEngine);
            flutterEngine.getLifecycleChannel().appIsResumed();
        } else {
            setContentView(layoutView);
        }

    }

    @Override
    public void dismiss() {
        flutterEngine.getLifecycleChannel().appIsDetached();
        super.dismiss();
    }

    protected abstract View getLayoutView();

    protected abstract String viewRout();

    protected void init() {

    }

}