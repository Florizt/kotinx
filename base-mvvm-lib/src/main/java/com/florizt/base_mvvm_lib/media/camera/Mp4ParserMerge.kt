package com.florizt.base_mvvm_lib.media.camera

import com.coremedia.iso.boxes.Container
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by wuwei
 * 2021/7/28
 * 佛祖保佑       永无BUG
 */
object Mp4ParserMerge {
    private const val PREFIX_VIDEO_HANDLER = "vide"
    private const val PREFIX_AUDIO_HANDLER = "soun"

    /**
     * 合并视频
     *
     * @param inputVideos
     * @param outputPath
     * @throws IOException
     */
    fun mergeVideos(
        inputVideos: List<String?>,
        outputPath: String?
    ) {
        try {
            val inputMovies = ArrayList<Movie>()
            for (input in inputVideos) {
                inputMovies.add(MovieCreator.build(input))
            }
            val videoTracks = LinkedList<Track>()
            val audioTracks = LinkedList<Track>()
            for (m in inputMovies) {
                for (t in m.getTracks()) {
                    if (PREFIX_AUDIO_HANDLER == t.getHandler()) {
                        audioTracks.add(t)
                    }
                    if (PREFIX_VIDEO_HANDLER == t.getHandler()) {
                        videoTracks.add(t)
                    }
                }
            }
            val outputMovie = Movie()
            if (audioTracks.size > 0) {
                outputMovie.addTrack(
                    AppendTrack(*audioTracks.toArray(arrayOfNulls<Track>(audioTracks.size)))
                )
            }
            if (videoTracks.size > 0) {
                outputMovie.addTrack(
                    AppendTrack(*videoTracks.toArray(arrayOfNulls<Track>(videoTracks.size)))
                )
            }
            val out: Container = DefaultMp4Builder().build(outputMovie)
            val fc: FileChannel = RandomAccessFile(outputPath, "rw").getChannel()
            out.writeContainer(fc)
            fc.close()
        } catch (e: Exception) {
        }
    }
}