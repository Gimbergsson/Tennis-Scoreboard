package se.dennisgimbergsson.tennisscoreboard.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import se.dennisgimbergsson.shared.extensions.logAndroidMessage

class WearDataListenerService : Service(),
    DataClient.OnDataChangedListener {

    private var count = 0

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    override fun onBind(intent: Intent): IBinder? {
        return null // Not a bound service
    }

    override fun onCreate() {
        super.onCreate()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Wearable.getDataClient(this).removeListener(this)
        scope.cancel()
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            // DataItem changed
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo("/count") == 0) {
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            updateCount(getInt(COUNT_KEY))
                        }
                        logAndroidMessage(message = "Data changed $count")
                        Toast.makeText(this, "Data changed $count", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (event.type == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    private fun updateCount(count: Int) {
        this.count = count
    }

    companion object {
        const val COUNT_KEY = "se.dennisgimbergsson.tennisscoreboard"
    }
}