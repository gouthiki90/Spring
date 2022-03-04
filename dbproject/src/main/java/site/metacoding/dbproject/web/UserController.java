package site.metacoding.dbproject.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import site.metacoding.dbproject.domain.user.User;
import site.metacoding.dbproject.domain.user.UserRepository;

@Controller
public class UserController {

    private UserRepository userRepository;

    // DI 받는 코드
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입 페이지(정적) - 누구나 들어갈 수 있음, 로그인X
    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // useranme=ssar$password=1234&email=ssar@nate.com (x-www-form)
    // 회원가입 액션 - 로그인X
    @PostMapping("/join") // 인증이 필요한 것이기 때문에 테이블명 안 적음
    public String join(User user) { // user오브젝트에 있는 필드를 사용할 수 있다.
        System.out.println("user : " + user);
        User userEntity = userRepository.save(user); // post로 데이터 들어왔을 때 필드 찾고 직접 넣어줌
        System.out.println("userEntity" + userEntity);
        return "redirect:/loginForm"; // 로그인 페이지로 이동해주는 컨트롤러 메서드를 재활용
    }

    // 로그인 페이지(정적) - 로그인X
    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    // SELECT * FROM user WHERE username=? AND passwrod=?
    // 원래 SELCT는 무조건 get 요청인데 로그인만 post로 예외이다.
    // 주소에 패스워드를 남길 수 없기 때문이다.
    // 로그인X
    @PostMapping("/login") // user 안 적음
    public String login(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(); // 세션 영역에 접근 sessionId : 85

        // 1. DB 연결해서 username, password있는 지 확인
        User userEntity = userRepository.mLogin(user.getUsername(), user.getPassword());
        System.out.println("사용자로 받은 username과 패스워드" + user);
        if (userEntity == null) { // 검증
            System.out.println("아이디 혹은 패스워드가 틀렸습니다.");
            System.out.println(userEntity);
        } else {
            System.out.println("로그인 되었습니다.");
            session.setAttribute("principal", userEntity); // 세션 영역에 세션 저장
        }
        // 2. 있으면 session 영역에 인증됨이라고 메시지 하나 넣기
        return "redirect:/"; // PostController 만들고 수정하기
    }

    // 유저 정보 페이지, 동적 페이지이기 때문에 id로 검색
    // 로그인 O
    @GetMapping("/user/{id}")
    public String detail(@PathVariable int id) {
        return "user/detail"; // 리턴값 상대경로
    }

    // 유저 수정 페이지
    // 로그인 O
    @GetMapping("/user/{id}/updateForm")
    public String updateForm(@PathVariable int id) {
        return "user/updateForm"; // 리턴값 상대경로
    }

    // 유저수정을 수행
    // 로그인O
    @PutMapping("/user/{id}")
    public String update(@PathVariable int id) {
        return "redirect:/userInfo" + id;
    }

    // 로그아웃
    // 로그인O
    @GetMapping("/logout")
    public String logout() {
        return "메인페이지를 돌려주면 됨"; // PostController 만들고 수정하기
    }
}
