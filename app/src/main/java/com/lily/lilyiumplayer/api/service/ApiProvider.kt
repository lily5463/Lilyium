package com.lily.lilyium.api.service

import com.lily.lilyiumplayer.api.service.ApiClient

object ApiProvider {
    private var api: NavidromeApi? = null

    fun getApi(): NavidromeApi {
        if (api == null) {
            api = ApiClient.create()
        }
        return api!!
    }
}