package ru.goodibunakov.amlabvideo.presentation.utils

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import ru.goodibunakov.amlabvideo.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class Utils {
    companion object {

        const val FUNCTION_SEARCH_YOUTUBE = "search?"
        const val FUNCTION_VIDEO_YOUTUBE = "videos?"
        const val FUNCTION_PLAYLIST_ITEMS_YOUTUBE = "playlistItems?"

        const val PARAM_KEY_YOUTUBE = "key="
        const val PARAM_CHANNEL_ID_YOUTUBE = "channelId="
        const val PARAM_PLAYLIST_ID_YOUTUBE = "playlistId="
        const val PARAM_VIDEO_ID_YOUTUBE = "id="
        const val PARAM_PART_YOUTUBE = "part="
        const val PARAM_PAGE_TOKEN_YOUTUBE = "pageToken="
        const val PARAM_ORDER_YOUTUBE = "order=date"
        const val PARAM_MAX_RESULT_YOUTUBE = "maxResults="
        const val PARAM_TYPE_YOUTUBE = "type=video"
        const val PARAM_FIELD_SEARCH_YOUTUBE = "fields=nextPageToken," +
                "pageInfo(totalResults),items(id(videoId),snippet(title,thumbnails,publishedAt))"
        const val PARAM_FIELD_VIDEO_YOUTUBE = "fields=pageInfo(totalResults)," +
                "items(contentDetails(duration))&"
        const val PARAM_FIELD_PLAYLIST_YOUTUBE = "fields=nextPageToken," +
                "pageInfo(totalResults),items(snippet(title,thumbnails,publishedAt,resourceId(videoId)))"
        const val PARAM_RESULT_PER_PAGE = 8

        const val ARRAY_PAGE_TOKEN = "nextPageToken"
        const val ARRAY_ITEMS = "items"

        const val OBJECT_ITEMS_ID = "id"
        const val OBJECT_ITEMS_CONTENT_DETAIL = "contentDetails"
        const val OBJECT_ITEMS_SNIPPET = "snippet"
        const val OBJECT_ITEMS_SNIPPET_THUMBNAILS = "thumbnails"
        const val OBJECT_ITEMS_SNIPPET_RESOURCE_ID = "resourceId"
        const val OBJECT_ITEMS_SNIPPET_THUMBNAILS_MEDIUM = "medium"

        const val KEY_VIDEO_ID = "videoId"
        const val KEY_TITLE = "title"
        const val KEY_PUBLISHED_AT = "publishedAt"
        const val KEY_URL_THUMBNAILS = "url"
        const val KEY_DURATION = "duration"

        const val ARG_TIMEOUT_MS = 4000

        const val TAG_FANDROID = "Fandroid:"

        const val TAG_CHANNEL_ID = "channel_id"
        const val TAG_VIDEO_TYPE = "video_type"
    }

    fun showSnackBar(view: View?, message: String?) {
        Snackbar.make(view!!, message!!, Snackbar.LENGTH_SHORT).show()
    }

    fun formatPublishedDate(activity: Activity, publishedDate: String?): String? {
        var result = Date()
        val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        try {
            result = df1.parse(publishedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return getTimeAgo(result, activity)
    }

    fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    fun getTimeAgo(date: Date?, ctx: Context): String? {
        if (date == null) {
            return null
        }
        val time = date.time
        val curDate = currentDate()
        val now = curDate.time
        if (time > now || time <= 0) {
            return null
        }
        val dim = getTimeDistanceInMinutes(time)
        var timeAgo: String?
        timeAgo = if (dim == 0) {
            ctx.resources.getString(R.string.date_util_term_less) + " " +
                    ctx.resources.getString(R.string.date_util_unit_minute)
        } else if (dim == 1 || dim == 21 || dim == 31 || dim == 41) {
            dim.toString() + " " + ctx.resources.getString(R.string.date_util_unit_minuta)
        } else if (dim == 2 || dim == 3 || dim == 4 || dim == 22 || dim == 23 || dim == 24 || dim == 32 || dim == 33 || dim == 34 || dim == 42 || dim == 43 || dim == 44) {
            dim.toString() + " " + ctx.resources.getString(R.string.date_util_unit_minute)
        } else if (dim in 5..20 || dim in 25..30 || dim in 35..40) {
            dim.toString() + " " + ctx.resources.getString(R.string.date_util_unit_minutes)
        } else if (dim in 45..89) {
            ctx.resources.getString(R.string.date_util_prefix_about) + " " +
                    ctx.resources.getString(R.string.date_util_unit_hour)
        } else if (dim in 90..270) {
            ctx.resources.getString(R.string.date_util_prefix_about) + " " +
                    (dim / 60.toFloat()).roundToLong() + " " +
                    ctx.resources.getString(R.string.date_util_unit_hour)
        } else if (dim in 271..1439) {
            ctx.resources.getString(R.string.date_util_prefix_about) + " " +
                    (dim / 60.toFloat()).roundToLong() + " " +
                    ctx.resources.getString(R.string.date_util_unit_hours)
        } else if (dim in 1440..2519) {
            "1 " + ctx.resources.getString(R.string.date_util_unit_daya)
        } else if (dim in 2520..6480) {
            (dim / 1440.toFloat()).roundToLong().toString() + " " +
                    ctx.resources.getString(R.string.date_util_unit_day)
        } else if (dim in 6481..29000 || dim in 34701..43200) {
            (dim / 1440.toFloat()).roundToLong().toString() + " " +
                    ctx.resources.getString(R.string.date_util_unit_days)
        } else if (dim in 29001..30500) {
            (dim / 1440.toFloat()).roundToLong().toString() + " " +
                    ctx.resources.getString(R.string.date_util_unit_daya)
        } else if (dim in 30501..34700) {
            (dim / 1440.toFloat()).roundToLong().toString() + " " +
                    ctx.resources.getString(R.string.date_util_unit_day)
        } else if (dim in 43201..86399) {
            ctx.resources.getString(R.string.date_util_prefix_about) + " " +
                    ctx.resources.getString(R.string.date_util_unit_month)
        } else if (dim in 86400..216000) {
            (dim / 43200.toFloat()).roundToLong().toString() + " " +
                    ctx.resources.getString(R.string.date_util_unit_month)
        } else if (dim in 216001..492480) {
            (dim / 43200.toFloat()).roundToLong().toString() + " " +
                    ctx.resources.getString(R.string.date_util_unit_months)
        } else if (dim in 492481..518400) {
            ctx.resources.getString(R.string.date_util_prefix_about) + " " +
                    ctx.resources.getString(R.string.date_util_unit_year)
        } else if (dim in 518401..914399) {
            ctx.resources.getString(R.string.date_util_prefix_over) + " " +
                    ctx.resources.getString(R.string.date_util_unit_year)
        } else if (dim in 914400..1051199) {
            ctx.resources.getString(R.string.date_util_prefix_almost) + " 2 " +
                    ctx.resources.getString(R.string.date_util_unit_years)
        } else {
            ctx.resources.getString(R.string.date_util_prefix_about) + " " +
                    (dim / 525600.toFloat()).roundToLong() + " " +
                    ctx.resources.getString(R.string.date_util_unit_years)
        }
        return timeAgo + " " + ctx.resources.getString(R.string.date_util_suffix)
    }

    private fun getTimeDistanceInMinutes(time: Long): Int {
        val timeDistance = currentDate().time - time
        return (abs(timeDistance) / 1000 / 60.toFloat()).roundToInt()
    }

    fun getTimeFromString(duration: String): String {
        var time = ""
        var hourexists = false
        var minutesexists = false
        var secondsexists = false
        if (duration.contains("H")) hourexists = true
        if (duration.contains("M")) minutesexists = true
        if (duration.contains("S")) secondsexists = true
        if (hourexists) {
            var hour: String
            hour = duration.substring(duration.indexOf("T") + 1,
                    duration.indexOf("H"))
            if (hour.length == 1) hour = "0$hour"
            time += "$hour:"
        }
        if (minutesexists) {
            var minutes: String
            minutes = if (hourexists) duration.substring(duration.indexOf("H") + 1,
                    duration.indexOf("M")) else duration.substring(duration.indexOf("T") + 1,
                    duration.indexOf("M"))
            if (minutes.length == 1) minutes = "0$minutes"
            time += "$minutes:"
        } else {
            time += "00:"
        }
        if (secondsexists) {
            var seconds: String
            seconds = if (hourexists) {
                if (minutesexists) duration.substring(duration.indexOf("M") + 1,
                        duration.indexOf("S")) else duration.substring(duration.indexOf("H") + 1,
                        duration.indexOf("S"))
            } else if (minutesexists) duration.substring(duration.indexOf("M") + 1,
                    duration.indexOf("S")) else duration.substring(duration.indexOf("T") + 1,
                    duration.indexOf("S"))
            if (seconds.length == 1) seconds = "0$seconds"
            time += seconds
        } else {
            time += "00"
        }
        return time
    }
}