package com.lambda.debugthis.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lambda.debugthis.R
import com.lambda.debugthis.adapters.ShoppingListAdapter
import com.lambda.debugthis.data.ShoppingRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ShoppingRepository.createShoppingList()

        rv_shopping_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ShoppingListAdapter()
        }

        btn_to_cart.setOnClickListener {
            fun getList(): String {
                var listString = ""
                for (item in ShoppingRepository.shoppingList) {
                    if (item.isAdded) listString += item.itemName + ", "
                }
                listString = listString.trimEnd(',', ' ')

                return listString
            }

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Shopping List: ${getList()}")
                type = "text/plain"
            }

            startActivity(sendIntent)

            val channelId = "${this.packageName}.notificationchannel"
            val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Shopping List Notification Channel"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val description = "Channel to send shopping list notification"

                val channel = NotificationChannel(channelId, name, importance)
                channel.description = description

                notificationManager.createNotificationChannel(channel)
            }

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSmallIcon(android.R.drawable.ic_menu_agenda)
                .setContentTitle("Confirmation")
                .setContentText("Shopping list has been created.")
                .setAutoCancel(true)
            notificationManager.notify(1, notificationBuilder.build())
        }
    }
}