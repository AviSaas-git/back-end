package AviSaaS.API.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConditionnementResponse {
    // Par calibre
    private int alveolesPetit;   private int restesPetit;
    private int alveolesMovyen;  private int restesMovyen;
    private int alveolesGros;    private int restesGros;
    private int alveolesSupGros; private int restesSupGros;
    // Total
    private int totalAlveoles;
    private int cartonsPossibles;
    private int alveolesRestantes;
}