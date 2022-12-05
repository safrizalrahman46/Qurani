package io.qurani.android.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.qurani.android.R
import io.qurani.android.databinding.ItemMeetingBinding
import io.qurani.android.manager.Utils
import io.qurani.android.model.Meeting
import io.qurani.android.model.MeetingReserve


class MeetingRvAdapter(meetings: List<Meeting>) :
    io.qurani.android.manager.adapter.BaseArrayAdapter<Meeting, MeetingRvAdapter.ViewHolder>(meetings) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMeetingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val meeting = items[position]
        viewholder.binding.itemMeetingNameTv.text = meeting.user.name
        viewholder.binding.itemMeetingEmailTv.text = meeting.user.email
        viewholder.binding.itemMeetingDateTv.text = Utils.getDateWithDayFromTimestamp(meeting.date)
        val time = meeting.time.start + "-" + meeting.time.end
        viewholder.binding.itemMeetingTimeTv.text = time
        viewholder.binding.itemMeetingStatusTv.text = meeting.status

        when (meeting.status) {
            MeetingReserve.Status.CANCELED.value -> {
                viewholder.binding.itemMeetingStatusTv.setBackgroundResource(R.drawable.round_view_red_corner10)
            }

            MeetingReserve.Status.FINISHED.value -> {
                viewholder.binding.itemMeetingStatusTv.setBackgroundResource(R.drawable.round_view_accent_corner10)
            }
        }

        if (meeting.user.avatar != null) {
            Glide.with(viewholder.itemView.context).load(meeting.user.avatar)
                .into(viewholder.binding.itemMeetingImg)
        }
    }

    class ViewHolder(val binding: ItemMeetingBinding) :
        RecyclerView.ViewHolder(binding.root)
}