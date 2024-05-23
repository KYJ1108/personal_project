package com.example.personal_project.user;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SendEmailService {
    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "kimyeji546408@gmail.com";
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void createMailAndChargePassword(UserPwRequestDto userPwRequestDto) throws CustomException {
        String str = getTempPassword();
        MailDto dto = new MailDto();
        dto.setAddress(userPwRequestDto.getEmail());
        dto.setTitle(userPwRequestDto.getLoginId() + "님의 임시비밀번호 안내 이메일 입니다.");
        dto.setMessage("안녕하세요. 임시비밀번호 안내 관련 메일입니다. \n" + userPwRequestDto.getLoginId() + "님의 임시비밀번호는 [ " + str + "] 입니다.");
        updatePassword(str, userPwRequestDto);
    }

    public void updatePassword(String str, UserPwRequestDto requestDto){
        String pw = passwordEncoder.encode(str);
        Long userId = Long.valueOf(requestDto.getEmail());
    }

    public String getTempPassword(){
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    StringBuilder stringBuilder = new StringBuilder();
    int idx;
    for (int i=0; i<10; i++) {
        idx = (int) (charSet.length * Math.random());
        stringBuilder.append(charSet[idx]);
    }
    return stringBuilder.toString();
    }

    public void mailSend(User user, String subject, String text){
        System.out.println("이메일 전송 완료");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
