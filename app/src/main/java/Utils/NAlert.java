package Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
/*
    2023.4.2 - showToastShort(), showToastLong() 추가
*/
public class NAlert {

    // 토스트 실행 (Short)
    public static void showToastShort(Context context, String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // 토스트 실행 (Long)
    public static void showToastLong(Context context, String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}