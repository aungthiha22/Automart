package com.rebook.automart.util

import android.annotation.SuppressLint
import android.app.Activity
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.rebook.automart.R

/**
 * Created by Dell on 5/22/2019.
 */
class BadgeUtil {
    companion object {
        @SuppressLint("PrivateResource")
        fun addBadge(position: Int, navigation: BottomNavigationView, activity: Activity, value: String) {
            // get badge container (parent)
            val bottomMenu = navigation.getChildAt(0) as? BottomNavigationMenuView
            val v = bottomMenu?.getChildAt(position) as? BottomNavigationItemView

            // inflate badge from layout
            var badge = LayoutInflater.from(activity)
                    .inflate(R.layout.badge_layout, bottomMenu, false)

            var txtVaulue = badge.findViewById<TextView>(R.id.txtValue)
            txtVaulue.text = value

            // create badge layout parameter
            val badgeLayout: FrameLayout.LayoutParams = FrameLayout.LayoutParams(badge.layoutParams).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                //topMargin = activity.resources.getDimension(R.dimen.design_bottom_navigation_margin).toInt()
                topMargin = activity.resources.getDimension(R.dimen.badge_top_margin).toInt()

                // <dimen name="bagde_left_margin">8dp</dimen>
                leftMargin = activity.resources.getDimension(R.dimen.bagde_left_margin).toInt()
            }

            // add view to bottom bar with layout parameter
            v?.addView(badge, badgeLayout)
        }
    }
}