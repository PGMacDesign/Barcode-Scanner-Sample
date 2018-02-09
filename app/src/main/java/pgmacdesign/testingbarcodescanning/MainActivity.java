package pgmacdesign.testingbarcodescanning;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.google.gson.Gson;
import com.google.zxing.Result;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.PermissionUtilities;

/**
 * Simple app for showing how to utilize a barcode scanner
 */
public class MainActivity extends AppCompatActivity {

    private FrameLayout activity_main_frame_layout;
    private CodeScannerView scannerView;
    private CodeScanner mCodeScanner;
    private boolean isShowingScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.isShowingScanner = false;
        this.activity_main_frame_layout = (FrameLayout) this.findViewById(
                R.id.activity_main_frame_layout);
        this.activity_main_frame_layout.setVisibility(View.INVISIBLE);
        this.scannerView = findViewById(R.id.scanner_view);
        setupScannerView();
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScanner();
            }
        });
    }

    private void setupScannerView(){
        mCodeScanner = CodeScanner.builder()
                /*camera can be specified by calling .camera(cameraId),
                first back-facing camera on the device by default*/
                /*code formats*/
                .formats(CodeScanner.ALL_FORMATS)/*List<BarcodeFormat>*/
                /*or .formats(BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX, ...)*/
                /*or .format(BarcodeFormat.QR_CODE) - only one format*/
                /*auto focus*/
                .autoFocus(true).autoFocusMode(AutoFocusMode.SAFE).autoFocusInterval(2000L)
                /*flash*/
                .flash(false)
                /*decode callback*/
                .onDecoded(new DecodeCallback() {
                    @Override
                    public void onDecoded(@NonNull final Result result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handleResult(result);
                            }
                        });
                    }
                })
                /*error callback*/
                .onError(new ErrorCallback() {
                    @Override
                    public void onError(@NonNull Exception error) {
                        handleResult(error);
                    }
                }).build(this, scannerView);

    }

    private void showScanner(){
        if(!PermissionUtilities.checkGrantedPermissions(MainActivity.this,
                new PermissionUtilities.permissionsEnum[]{
                        PermissionUtilities.permissionsEnum.CAMERA})){
            PermissionUtilities.permissionsRequestShortcut(
                    MainActivity.this, new PermissionUtilities.permissionsEnum[]{
                            PermissionUtilities.permissionsEnum.CAMERA});
        } else {
            activity_main_frame_layout.setVisibility(View.VISIBLE);
            mCodeScanner.startPreview();
            isShowingScanner = true;
        }
    }

    private void handleResult(com.google.zxing.Result result){
        L.m("result == " + new Gson().toJson(result, Result.class));
        Toast.makeText(MainActivity.this, result.getText(),
                Toast.LENGTH_LONG).show();
        closeScannerView();
    }

    private void handleResult(Exception error){
        L.toast(MainActivity.this, "Error scanning");
        error.printStackTrace();
        closeScannerView();
    }

    private void closeScannerView(){
        try {
            activity_main_frame_layout.setVisibility(View.INVISIBLE);
            mCodeScanner.stopPreview();
            isShowingScanner = false;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(isShowingScanner){
            closeScannerView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


}
