package raven.forms;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Home extends JPanel {

    private List<ModelLocation> locations;
    private int index = 0;
    private HomeOverlay homeOverlay;

    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;

    public Home() {
        init();
        testData();
    }

    private void init() {
        factory = new MediaPlayerFactory();
        mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
        Canvas canvas = new Canvas();
        mediaPlayer.videoSurface().set(factory.videoSurfaces().newVideoSurface(canvas));
        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                if (newTime >= mediaPlayer.status().length() - 1000) {
                    mediaPlayer.controls().setPosition(0);
                }
            }
        });
        setLayout(new BorderLayout());
        add(canvas);
    }

    private void testData() {
        locations = new ArrayList<>();
        locations.add(new ModelLocation("Serendipity Cove\nRetreat",
                "Serendipity Cove Retreat is a secluded haven nestled amidst untouched nature. Surrounded by lush forests, cascading waterfalls, and serene lakes, this enchanting retreat offers a tranquil escape for those seeking solace and rejuvenation. With rustic cabins, modern amenities, and breathtaking views, Serendipity Cove Retreat promises a unique and unforgettable experience in the heart of nature.",
                "video/video 1.mp4"));

        locations.add(new ModelLocation("Whispering Pines\nRetreat",
                "Nestled in a serene forest, Whispering Pines Retreat is a peaceful haven surrounded by towering trees and breathtaking landscapes. This tranquil getaway offers a chance to reconnect with nature, unwind, and find inner peace. With cozy cabins, stunning views, and a soothing atmosphere, Whispering Pines Retreat is the perfect destination for relaxation and rejuvenation.",
                "video/video 2.mp4"));

        locations.add(new ModelLocation("Serenity Cove\nResort",
                "Situated on the serene shores of a crystal-clear lake, Serenity Cove Resort is a picturesque haven that offers a perfect blend of relaxation and adventure. With luxurious accommodations, breathtaking views, and a wide range of recreational activities, this idyllic retreat provides an escape from the ordinary. Whether you're seeking tranquility by the water, exploring nature trails, or indulging in spa treatments, Serenity Cove Resort promises an unforgettable experience that rejuvenates the mind, body, and soul.",
                "video/video 3.mp4"));
    }

    public void initOverlay(JFrame frame) {
        homeOverlay = new HomeOverlay(frame, locations);
        homeOverlay.getOverlay().setEventHomeOverlay(index1 -> {
            play(index1);
        });
        mediaPlayer.overlay().set(homeOverlay);
        mediaPlayer.overlay().enable(true);
    }

    public void play(int index) {
        this.index = index;
        ModelLocation location = locations.get(index);
        if (mediaPlayer.status().isPlaying()) {
            mediaPlayer.controls().stop();
        }
        mediaPlayer.media().play(location.getVideoPath());
        mediaPlayer.controls().play();
        homeOverlay.getOverlay().setIndex(index);
    }

    public void stop() {
        mediaPlayer.controls().stop();
        mediaPlayer.release();
        factory.release();
    }
}
