package com.example.discordChatDeleter;

import org.springframework.beans.factory.annotation.Value;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;

@SpringBootApplication
public class DiscordChatDeleterApplication implements CommandLineRunner {

	// 봇 토큰을 여기에 입력합니다.
	@Value("${DISCORD_BOT_TOKEN}")
	private String BOT_TOKEN;

	public static void main(String[] args) {
		SpringApplication.run(DiscordChatDeleterApplication.class, args);
	}

	@Override
	public void run(String... args) throws LoginException {
		// 봇 초기화 및 리스너 등록
		JDABuilder.createDefault(BOT_TOKEN)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(new UserChatDeleterBot())
				.build();	}
}
