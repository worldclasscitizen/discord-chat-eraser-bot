package com.example.discordChatDeleter;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.List;
import java.util.stream.Collectors;

public class UserChatDeleterBot extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // 봇이 보낸 메시지는 무시
        if (event.getAuthor().isBot()) return;

        System.out.println("Received message: " + event.getMessage().getContentRaw());

        // "!삭제" 명령어를 입력했을 때 처리
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!삭제")) {
            TextChannel channel = event.getChannel().asTextChannel();
            channel.getIterableHistory().queue(messages -> {
                // 해당 채널의 기록에서 명령어를 보낸 사용자의 메시지만 필터링
                List<Message> userMessages = messages.stream()
                        .filter(msg -> msg.getAuthor().getId().equals(event.getAuthor().getId()))
                        .collect(Collectors.toList());

                // 각 메시지 삭제
                for (Message msg : userMessages) {
                    msg.delete().queue();
                }
                // 삭제 완료 후 확인 메시지 전송
                channel.sendMessage("총 " + userMessages.size() + "개의 메시지를 삭제했습니다.").queue();
            });
        }
    }
}
