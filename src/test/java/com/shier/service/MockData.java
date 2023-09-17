package com.shier.service;

import com.shier.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockData {
    @Resource
    private UserService userService;


    private static final String[] avatarUrls = {
            "http://niu.ochiamalu.xyz/12d4949b4009d089eaf071aef0f1f40.jpg",
            "http://niu.ochiamalu.xyz/1bff61de34bdc7bf40c6278b2848fbcf.jpg",
            "http://niu.ochiamalu.xyz/22fe8428428c93a565e181782e97654.jpg",
            "http://niu.ochiamalu.xyz/75e31415779979ae40c4c0238aa4c34.jpg",
            "http://niu.ochiamalu.xyz/905731909dfdafd0b53b3c4117438d3.jpg",
            "http://niu.ochiamalu.xyz/a84b1306e46061c0d664e6067417e5b.jpg",
            "http://niu.ochiamalu.xyz/b93d640cc856cb7035a851029aec190.jpg",
            "http://niu.ochiamalu.xyz/c11ae3862b3ca45b0a6cdff1e1bf841.jpg",
            "http://niu.ochiamalu.xyz/cccfb0995f5d103414bd8a8bd742c34.jpg",
            "http://niu.ochiamalu.xyz/f870176b1a628623fa7fe9918b862d7.jpg"};
    private static final String[] OTHER_TAGS = {"\"java\"", "\"python\"", "\"c\"", "\"c++\"", "\"c#\"", "\"html/css\"", "\"vue\"", "\"react\""};
    private static final String[] SCHOOL_YEARS = {"\"高一\"", "\"高二\"", "\"高三\"", "\"大一\"", "\"大二\"", "\"大三\"", "\"大四\"", "\"研究生\"", "\"已工作\""};
    private static final String[] FIRST_NAMES = {"Alice", "Bob", "Charlie", "David", "Emily", "Frank", "Grace", "Henry", "Isabella", "Jack", "Kate", "Liam", "Mia", "Nathan", "Olivia", "Peter", "Quinn", "Rachel", "Sarah", "Tyler", "Ursula", "Victoria", "William", "Xander", "Yvonne", "Zachary"};
    private static final String[] LAST_NAMES = {"Anderson", "Brown", "Clark", "Davis", "Evans", "Ford", "Garcia", "Harris", "Isaacs", "Johnson", "Klein", "Lee", "Miller", "Nguyen", "O'Brien", "Parker", "Quinn", "Roberts", "Smith", "Taylor", "Ueda", "Valdez", "Williams", "Xu", "Yamamoto", "Zhang"};
    private static final String[] EMAIL_DOMAINS = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "aol.com", "icloud.com", "protonmail.com", "yandex.com", "mail.com", "inbox.com"};
    private static final String[] GENDER = {"\"男\"", "\"女\"", "\"保密\""};

    @Test
    void insert() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            String randomUsername = getRandomString(10);
            ArrayList<String> randomTags = getRandomTags(random);
            String randomEmail = randomUsername + "@" + EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)];
            String randomProfile = getRandomProfile();
            String randomPhone = getRandomPhone();
            User user = new User();
            user.setId(null);
            user.setUsername(randomUsername);
            user.setPassword("11111111");
            user.setUserAccount(randomUsername.toLowerCase());
            user.setAvatarUrl(avatarUrls[random.nextInt(avatarUrls.length)]);
            user.setEmail(randomEmail);
            user.setProfile(randomProfile);
            user.setPhone(randomPhone);
            user.setTags(randomTags.toString());
            user.setRole(0);
            user.setGender(random.nextInt(2));
            user.setStatus(0);
            user.setIsDelete(0);
            userService.save(user);
        }
    }


    private static String getRandomPhone() {
        Random random = new Random();
        String phoneNumber = "1";
        for (int j = 0; j < 10; j++) {
            phoneNumber += random.nextInt(10);
        }
        return phoneNumber;
    }

    private static String getRandomProfile() {
        String[] adjectives = {"开心的", "难过的", "刺激的", "无聊的", "有趣的", "搞笑的", "严肃的", "有创意的", "懒惰的", "充满活力的", "美丽的", "聪明的", "勇敢的", "诚实的", "慷慨的", "有趣的", "有创意的", "有条理的", "有耐心的", "有决心的", "有毅力的", "有同情心的", "有幽默感的", "有冒险精神的", "有野心的", "有魅力的", "有自信的", "有智慧的", "有礼貌的", "有耳聪目明的", "有远见的", "有责任心的", "有领导才能的"};
        String[] nouns = {"学生", "教师", "程序员", "艺术家", "作家", "音乐家", "运动员", "厨师", "科学家", "企业家", "医生", "教师", "律师", "工程师", "会计师", "程序员", "销售员", "市场营销人员", "记者", "作家", "演员", "音乐家", "画家", "建筑师", "设计师", "厨师", "服务员", "警察", "消防员", "军人", "运动员", "教练", "经理", "企业家", "政治家", "科学家", "研究员", "救援人员"};
        Random random = new Random();
        String adjective = adjectives[random.nextInt(adjectives.length)];
        String noun = nouns[random.nextInt(nouns.length)];
        return "我是一个" + adjective + noun + "。";
    }

    private static String getRandomUsername(Random random) {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return (firstName + lastName);
    }

    private static ArrayList<String> getRandomTags(Random random) {
        ArrayList<String> randomTags = getRandomValue(OTHER_TAGS, random);
        randomTags.add(getRandomUniqueValue(SCHOOL_YEARS, random));
        String randomGender = getRandomGender(random);
        if (randomGender != null) {
            randomTags.add(randomGender);
        }
        return randomTags;
    }

    private static String getRandomGender(Random random) {
        int randomIndex = random.nextInt(4);
        if (randomIndex == 3) {
            return null;
        } else {
            return GENDER[randomIndex];
        }
    }

    private static ArrayList<String> getRandomValue(String[] values, Random random) {
        ArrayList<String> tagList = new ArrayList<>();
        int randomTagNum = random.nextInt(3) + 1;
        for (int i = 0; i < randomTagNum; i++) {
            tagList.add(values[random.nextInt(values.length)]);
        }
        return tagList;
    }

    private static String getRandomUniqueValue(String[] values, Random random) {
        return values[random.nextInt(values.length)];
    }

    private static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
