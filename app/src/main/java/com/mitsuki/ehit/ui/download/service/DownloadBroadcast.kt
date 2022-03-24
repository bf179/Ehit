package com.mitsuki.ehit.ui.download.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.model.entity.db.DownloadNode

object DownloadBroadcast {

    const val DOWNLOAD_BROADCAST_PAGE = "DOWNLOAD_PAGE_ACTION"
    const val DOWNLOAD_BROADCAST_THUMB = "DOWNLOAD_THUMB_ACTION"
    const val DOWNLOAD_BROADCAST_PAGE_START = "DOWNLOAD_BROADCAST_PAGE_START"
    const val DOWNLOAD_BROADCAST_STOP = "DOWNLOAD_BROADCAST_STOP"
    const val DOWNLOAD_BROADCAST_STOP_ALL = "DOWNLOAD_BROADCAST_STOP_ALL"


    const val FINISH_NODE = "FINISH_NODE"
    const val TASK_NAME = "TASK_NAME"
    const val TASK_TOTAL = "TASK_TOTAL"
    const val TASK_OVER = "TASK_OVER"

    fun registerReceiver(context: Context, receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter().apply {
                addAction(DOWNLOAD_BROADCAST_PAGE)
                addAction(DOWNLOAD_BROADCAST_THUMB)
                addAction(DOWNLOAD_BROADCAST_PAGE_START)
                addAction(DOWNLOAD_BROADCAST_STOP)
                addAction(DOWNLOAD_BROADCAST_STOP_ALL)
            })
    }

    fun unregisterReceiver(context: Context, receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }

    fun sendStart(name: String, total: Int, over: Int) {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            putExtra(TASK_NAME, name)
            putExtra(TASK_TOTAL, total)
            putExtra(TASK_OVER, over)
            action = DOWNLOAD_BROADCAST_PAGE_START
        })
    }

    fun sendFinish(node: DownloadNode) {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            putExtra(
                FINISH_NODE,
                DownloadNode(
                    node.gid,
                    node.token,
                    node.page,
                    1,
                    "${System.currentTimeMillis()}"
                )
            )
            action = DOWNLOAD_BROADCAST_PAGE
        })
    }

    fun sendThumbFinish() {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            //TODO 添加数据回调
            action = DOWNLOAD_BROADCAST_THUMB
        })
    }

    fun sendStop(gid: Long, token: String) {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            action = DOWNLOAD_BROADCAST_STOP
            putExtra(DownloadService.TARGET, gid to token)
        })
    }

    fun sendStopAll() {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            action = DOWNLOAD_BROADCAST_STOP_ALL
        })
    }
}