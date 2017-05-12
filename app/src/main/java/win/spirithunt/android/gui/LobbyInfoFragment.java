package win.spirithunt.android.gui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import net.glxn.qrgen.android.QRCode;
import win.spirithunt.android.R;

/**
 * @author Remco Schipper
 */

public class LobbyInfoFragment extends Fragment {
    private ImageView qrCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lobby_view_info, container, false);
        this.qrCode = (ImageView)view.findViewById(R.id.qr_code);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        Bitmap code = QRCode.from(bundle.getString("lobbyId")).withSize(1080, 1080).bitmap();
        this.qrCode.setImageBitmap(code);
    }
}
