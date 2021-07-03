package com.nicolas.firebaseapp.fragement;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDeepLinkBuilder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nicolas.firebaseapp.NavigationActivity;
import com.nicolas.firebaseapp.R;
import com.nicolas.firebaseapp.UpdateActivity;
import com.nicolas.firebaseapp.util.App;

import static com.nicolas.firebaseapp.util.App.CHANNEL_1;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    private NotificationManagerCompat notificationManagerCompat;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_notification, container, false);
        notificationManagerCompat = NotificationManagerCompat.from(getContext());

        EditText editTitle = layout.findViewById(R.id.frag_notification_title);
        EditText editMsg = layout.findViewById(R.id.frag_notification_msg);
        Button btnSend = layout.findViewById(R.id.frag_notification_btn_send);

        btnSend.setOnClickListener( v -> {
            String title = editTitle.getText().toString();
            String msg = editMsg.getText().toString();

            Intent intent = new Intent(getContext(), NavigationActivity.class);

            /* abrir a notificação da tela do celular */
            PendingIntent contentIntent = new NavDeepLinkBuilder(getContext())
                                          .setComponentName(NavigationActivity.class)
                                          .setGraph(R.navigation.nav_graph)
                                           .setDestination(R.id.nav_menu_lista_imagens)
                                            .createPendingIntent();

            /*PendingIntent contentIntent = PendingIntent.getActivity(
                    getContext(),
                    0,
                     intent,
                    0
            );*/

            /* Criar a notificação */
            Notification notification = new NotificationCompat
                    .Builder(getContext(), CHANNEL_1)
                    .setSmallIcon(R.drawable.ic_account_circle_black_24dp)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(contentIntent)
                    .addAction(R.drawable.ic_account_circle_black_24dp,
                                "Toast",
                                actionIntent)
                    .build();

            notificationManagerCompat.notify(1,notification);

        });

        return layout;
    }
}

