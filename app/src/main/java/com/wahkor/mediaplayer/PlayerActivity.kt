package com.wahkor.mediaplayer

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.model.Song

var currentSong = 0
lateinit var mediaPlayer: MediaPlayer

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity() {
    private var isInitial=false
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    private lateinit var tvPass: TextView
    private lateinit var tvDue: TextView
    private lateinit var playBTN: ImageView
    private lateinit var titleView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var playlistListView: RecyclerView
    private var adapter= PlayListRecyclerAdapter(SongList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playBTN = findViewById(R.id.playerPlay)
        titleView = findViewById(R.id.playerTitle)
        tvDue = findViewById(R.id.tv_due)
        tvPass = findViewById(R.id.tv_pass)
        seekBar = findViewById(R.id.playerSeekbar)
        playlistListView=findViewById(R.id.playerListView)
        playlistListView.layoutManager=LinearLayoutManager(this)
        playlistListView.adapter=adapter
        initial()
        //SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                   mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
        mediaPlayer.setOnCompletionListener {  nextClick(playBTN) }
    }

    private fun initial() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            playlistListView.visibility=View.GONE
        } else {
            // In portrait
            setPlayerDetail()
            playlistListView.visibility=View.VISIBLE
        }
        isInitial=true
        seekBar.max = mediaPlayer.duration
        adapter.notifyDataSetChanged()
        if (mediaPlayer.isPlaying) {
            setRunnable()
        }
        setPlayButton()
        titleView.text = SongList[currentSong].TITLE
    }


    private fun setPlayButton( ){
        if(mediaPlayer.isPlaying){
            playBTN.setImageDrawable(
                ContextCompat.getDrawable(this,
                    R.drawable.ic_baseline_pause
                )
            )

        }else{
            playBTN.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_play
                )
            )
        }

    }
    fun playClick(view: View) {

        setupPlayer()
        initial()
    }

    fun prevClick(view: View) {
        getNewPosition(currentSong-1)
        preparePlayer()
    }

    fun nextClick(view: View) {
        getNewPosition(currentSong+1)
        preparePlayer()
    }
private fun getNewPosition(position: Int){
    currentSong= when {
        position<0 -> SongList.size-1
        position> SongList.size-1 -> 0
        else -> position
    }
}
    private fun preparePlayer() {
        mediaPlayer.pause()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(SongList[currentSong].DATA)
        mediaPlayer.prepare()
        setupPlayer()
        initial()
    }

    private fun getTimeInMinute(time: Int): String {
        var secs = time / 1000
        var minutes = secs / 60
        val hours = minutes / 60
        minutes -= hours * 60
        secs = secs - minutes * 60 - hours * 60 * 60
        return "${if (hours == 0) "" else "$hours:"}${if (minutes < 10) "0$minutes:" else "$minutes:"}${if (secs < 10) "0$secs" else "$secs"}"
    }

    private fun setupPlayer() {
        if (mediaPlayer.isPlaying) { mediaPlayer.pause()} else {mediaPlayer.start()}
        setPlayButton()
        titleView.text = SongList[currentSong].TITLE
    }

    private fun setRunnable() {
        val string = SongList[currentSong].TITLE!!
        titleView.text = if(string.length>40) string.substring(0,40) else string

        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentPosition
            tvPass.text = getTimeInMinute(mediaPlayer.currentPosition)
            tvDue.text = getTimeInMinute(mediaPlayer.duration - mediaPlayer.currentPosition)
            handler.postDelayed(runnable, 1000)

        }
        handler.postDelayed(runnable, 1000)

    }

    override fun onBackPressed() {
    }
    inner class PlayListRecyclerAdapter(private var playlistList: ArrayList<Song>):RecyclerView.Adapter<PlayListViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
            val itemView:View=LayoutInflater.from(parent.context).inflate(R.layout.play_list_layout,parent,false)
            return PlayListViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return playlistList.size
        }

        override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
            val song=playlistList[position]
            holder.binding(song)
        }

    }
    inner class PlayListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private val titlePlaylist: TextView =itemView.findViewById(R.id.playlistTitle)
        fun binding(track:Song){
            titlePlaylist.text=track.TITLE
            if (SongList[currentSong].DATA==track.DATA){
                itemView.setBackgroundColor(getColor(R.color.selected_playlist))
            }else{
                itemView.setBackgroundColor(getColor(R.color.unselected_playlist))

            }
            itemView.setOnClickListener {
                mediaPlayer.pause()
                currentSong=position
                preparePlayer()
                initial()
            }
        }

    }
private fun setPlayerDetail(){
    val song=SongList[currentSong]
    findViewById<TextView>(R.id.playerDetail_name).text=song.TITLE
    findViewById<TextView>(R.id.playerDetail_artist).text= song.ARTIST
    findViewById<TextView>(R.id.playerDetail_album).text=song.ALBUM
    findViewById<TextView>(R.id.playerDetail_duration).text= getTimeInMinute(song.DURATION!!.toInt())
}
    fun playListDropDown(view: View) {
        val detail=findViewById<LinearLayout>(R.id.playerDetailLayout)
        val show=findViewById<ImageView>(R.id.playerShowDetail)
        if(detail.visibility==View.VISIBLE){
            detail.visibility=View.GONE
            show.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        }else{
            detail.visibility=View.VISIBLE
            show.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
        }

    }}