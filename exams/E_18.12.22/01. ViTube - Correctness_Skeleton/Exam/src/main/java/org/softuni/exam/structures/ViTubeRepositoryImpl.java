package org.softuni.exam.structures;

import org.softuni.exam.entities.User;
import org.softuni.exam.entities.Video;

import java.util.*;
import java.util.stream.Collectors;

public class ViTubeRepositoryImpl implements ViTubeRepository {

    /*private List<User> userRepository;
    private List<Video> videoRepository;*/

    private Map<User, List<Video>> userContainer;
    private Map<User, Map<String, Integer>> userStats;
    private List<Video> videoContainer;

    public ViTubeRepositoryImpl() {
        //this.userRepository = new ArrayList<>();
        userContainer = new LinkedHashMap<>();
        videoContainer = new ArrayList<>();
        userStats = new LinkedHashMap<>();
    }

    @Override
    public void registerUser(User user) {
        if (user != null) {
            //this.userRepository.add(user);
            userContainer.putIfAbsent(user, new ArrayList<>());
            userStats.putIfAbsent(user, new HashMap<>());

            userStats.get(user).put("Views", 0);
            userStats.get(user).put("L/D", 0);
            //userStats.get(user).put("Dislikes", 0);
        }
    }

    @Override
    public void postVideo(Video video) {
        if (video != null) {
            this.videoContainer.add(video);
            //videoContainer.putIfAbsent(video, new ArrayList<>());
        }


    }

    @Override
    public boolean contains(User user) {
        //return this.userRepository.contains(user);
        return userContainer.containsKey(user);
    }

    @Override
    public boolean contains(Video video) {
        return this.videoContainer.contains(video);
        //return videoContainer.containsKey(video);
    }

    @Override
    public Iterable<Video> getVideos() {
        return this.videoContainer;
        //return videoContainer.keySet();
    }

    @Override
    public void watchVideo(User user, Video video) throws IllegalArgumentException {
        ensureElementContained(user, video);

        //video.setViews(video.getViews() + 1);

        List<Video> videos = userContainer.get(user);

        updateViews(video, videos);
        updateViews(video, videoContainer);
        setStat(user, "Views", 1);
        /*for (Video curr : videos) {
            if (curr.getId().equals(video.getId())) {
                curr.setViews(curr.getViews() + 1);
            }
        }*/
    }

    @Override
    public void likeVideo(User user, Video video) throws IllegalArgumentException {
        ensureElementContained(user, video);
        //video.setLikes(video.getLikes() + 1);

        List<Video> videos = userContainer.get(user);
        updateLikes(video, videos);
        updateLikes(video, videoContainer);

        setStat(user, "L/D", 1);
        /*for (Video curr : videos) {
            if (curr.getId().equals(video.getId())) {
                curr.setLikes(curr.getLikes() + 1);
                videoContainer.set()
            }
        }*/

    }

    @Override
    public void dislikeVideo(User user, Video video) throws IllegalArgumentException {
        ensureElementContained(user, video);

        List<Video> videos = userContainer.get(user);

        updateDislikes(video, videos);
        updateDislikes(video, videoContainer);

        setStat(user, "L/D", 1);

    }

    @Override
    public Iterable<User> getPassiveUsers() {
        return userContainer.entrySet().stream()
                .filter(e -> e.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Video> getVideosOrderedByViewsThenByLikesThenByDislikes() {
        if (videoContainer.isEmpty()) {
            return new ArrayList<>();
        } else {
            videoContainer.sort(Comparator.comparing(Video::getViews)
                    .reversed()
                    .thenComparing(Video::getLikes)
                    .reversed()
                    .thenComparing(Video::getDislikes));
            return videoContainer;
        }
    }

    @Override
    public Iterable<User> getUsersByActivityThenByName() {
        if (userStats.isEmpty()) {
            return new ArrayList<>();
        } else {
            return userContainer.keySet().stream()
                    .filter(u -> !userContainer.get(u).isEmpty())
                    .sorted(Comparator.comparing((User u) -> userStats.get(u).get("Views"))
                            .reversed()
                            .thenComparing(u -> userStats.get(u).get("L/D"))
                            .reversed()
                            .thenComparing(User::getUsername))
                    .collect(Collectors.toList());
        }
    }

    private void ensureElementContained(User user, Video video) {
        if (!contains(user) || !contains(video)) {
            throw new IllegalArgumentException();
        }
    }

    private static void updateViews(Video video, List<Video> videos) {
        String currId = video.getId();
        videos.forEach(e -> {
            if (e.getId().equals(currId)) {
                e.setViews(e.getViews() + 1);
            }
        });
    }

    private static void updateLikes(Video video, List<Video> videos) {
        String currId = video.getId();
        videos.forEach(e -> {
            if (e.getId().equals(currId)) {
                e.setLikes(e.getLikes() + 1);
            }
        });
    }

    private static void updateDislikes(Video video, List<Video> videos) {
        String currId = video.getId();
        videos.forEach(e -> {
            if (e.getId().equals(currId)) {
                e.setDislikes(e.getDislikes() + 1);
            }
        });
    }

    private void setStat(User user, String key, int amount) {
        Map<String, Integer> entry = userStats.get(user);
        entry.put(key, entry.get(key) + amount);
    }
}
