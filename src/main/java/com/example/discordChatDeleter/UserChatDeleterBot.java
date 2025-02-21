package com.example.discordChatDeleter;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class UserChatDeleterBot extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("Received message: " + event.getMessage().getContentRaw());

        // 명령어를 공백 단위로 쪼개서 저장
        String[] tokens = event.getMessage().getContentRaw().split("\\s+");

        // 명령어가 "!삭제"라면,
        if (tokens[0].equals("!삭제")) {
            TextChannel channel = event.getChannel().asTextChannel();
            channel.getIterableHistory().queue(messages -> {
                // 해당 채널의 기록에서 명령어를 보낸 사용자의 메시지만 필터링
                List<Message> userMessages = messages.stream()
                        .filter(msg -> msg.getAuthor().getId().equals(event.getAuthor().getId()))
                        .collect(Collectors.toList());

                // 파라미터가 없는 경우: 모든 메시지 삭제
                if (tokens.length == 1) {
                    // 아무 추가 조건 없이 사용자 메시지 전부 삭제
                } else {
                    // 파라미터가 있을 경우: "YYYY-MM" 형식인지 확인 후 해당 월의 메시지만 삭제
                    String parameter = tokens[1];
                    try {
                        YearMonth targetMonth = YearMonth.parse(parameter);
                        // 사용자 메시지 중 targetMonth에 생성된 메시지만 필터링
                        List<Message> filteredMessages = userMessages.stream()
                                .filter(msg -> {
                                    YearMonth msgMonth = YearMonth.from(msg.getTimeCreated().toLocalDate());
                                    return msgMonth.equals(targetMonth);
                                })
                                .collect(Collectors.toList());
                        // 필터링 결과로 userMessages 대체
                        userMessages = filteredMessages;
                    } catch (DateTimeParseException e) {
                        channel.sendMessage("파라미터의 형식이 잘못되었습니다. 올바른 형식: YYYY-MM").queue();
                        return;
                    }
                }

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
