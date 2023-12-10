package core;

import models.Message;

import java.util.*;
import java.util.stream.Collectors;

public class DiscordImpl implements Discord {

    LinkedHashMap<String, Message> messages = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> channelsCnt = new LinkedHashMap<>();

    @Override
    public void sendMessage(Message message) {
        messages.put(message.getId(), message);

        String channel = message.getChannel();
        channelsCnt.putIfAbsent(channel, 0);
        channelsCnt.put(channel, channelsCnt.get(channel) + 1);
    }

    @Override
    public boolean contains(Message message) {
        return messages.get(message.getId()) != null;
    }

    @Override
    public int size() {
        return messages.size();
    }

    @Override
    public Message getMessage(String messageId) {
        Message message = messages.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException();
        }
        return message;
    }

    @Override
    public void deleteMessage(String messageId) {
        Message remove = messages.remove(messageId);

        if (remove == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void reactToMessage(String messageId, String reaction) {
        Message message = messages.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException();
        }

        message.getReactions().add(reaction);
    }

    @Override
    public Iterable<Message> getChannelMessages(String channel) {
        List<Message> filteredMessages = messages.values().stream()
                .filter(m -> m.getChannel().equals(channel))
                .collect(Collectors.toList());

        if (filteredMessages.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return filteredMessages;
    }

    @Override
    public Iterable<Message> getMessagesByReactions(List<String> reactions) {
        //returns all messages, which contain ALL  of the given reactions,
        List<Message> filteredMessages = new ArrayList<>();

        for (Message message : messages.values()) {
            boolean toAdd = true;
            for (String reaction : reactions) {
                if (!reactions.contains(reaction)) {
                    toAdd = false;
                    break;
                }
            }
            if (toAdd) {
                filteredMessages.add(message);
            }
        }

        //ordered by count of reactions in descending order and by timestamp in ascending order.
        return messages.values().stream()
                //.sorted(Comparator.comparing((Message e) -> e.getReactions().size()))
                .sorted((m1, m2) -> Integer.compare(m2.getReactions().size(), m1.getReactions().size()))
                .sorted(Comparator.comparing(Message::getTimestamp))
                        //.reversed()
                        //.thenComparing(Message::getTimestamp))
                .collect(Collectors.toList());

    }

    @Override
    public Iterable<Message> getMessageInTimeRange(Integer lowerBound, Integer upperBound) {
        // returns all of the messages with timestamp in the range specified with lower bound and upper bound.
        // Both bounds are inclusive.
        // The results should be ordered by count of total messages contained in each message’s channel, in descending order.
        // If there aren’t any messages in the specified range – return an empty collection.

        return messages.values().stream()
                .filter(m -> m.getTimestamp() >= lowerBound && m.getTimestamp() <= m.getTimestamp())
                .sorted(Comparator
                        .comparing((Message m) -> {
                            String channel = m.getChannel();
                            return channelsCnt.get(channel);
                        }).reversed())
                .collect(Collectors.toList());

    }

    @Override
    public Iterable<Message> getTop3MostReactedMessages() {
        return messages.values().stream()
                .sorted(Comparator
                        .comparing((Message m) -> m.getReactions().size())
                        .reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Message> getAllMessagesOrderedByCountOfReactionsThenByTimestampThenByLengthOfContent() {
        return messages.values().stream()
                .sorted(Comparator
                        .comparing((Message m) -> m.getReactions().size())
                        .reversed()
                        .thenComparing(m -> m.getTimestamp())
                        .thenComparing(m -> m.getContent().length()))
                .collect(Collectors.toList());
    }
}
