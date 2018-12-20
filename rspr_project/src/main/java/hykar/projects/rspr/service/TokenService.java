package hykar.projects.rspr.service;

import hykar.projects.rspr.entity.Token;
import hykar.projects.rspr.entity.User;
import hykar.projects.rspr.enums.TokenType;
import hykar.projects.rspr.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Optional;

@Service
public class TokenService {

    @Value("${rspr.security.token-strength}")
    private int tokenStrength;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenRepository tokenRepository;

    public void removeToken(Token token)
    {
        tokenRepository.delete(token);
    }

    public void removeTokens(User u)
    {
        Collection<Token> tokens = tokenRepository.getTokensByUser(u);
        tokenRepository.deleteAll(tokens);
    }

    public boolean consumeToken(String tokenString)
    {
        Optional<Token> tokenBox = tokenRepository.getToken(tokenString);

        if(!tokenBox.isPresent()) return  false;

        Token token = tokenBox.get();

        TokenType type = TokenType.resolveTokenType(token.getType());

        removeToken(token);
        if(type == TokenType.ACTIVATION)
            userService.activate(token.getUser());


        return true;


    }


    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    public Token generateToken(User user,TokenType type)
    {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[1<<(tokenStrength-3)];
        random.nextBytes(bytes);

        Token token = new Token();

        token.setToken( bytesToHex(bytes));
        token.setUser(user);
        token.setType(type.getType());

        tokenRepository.save(token);

        return token;
    }

}
