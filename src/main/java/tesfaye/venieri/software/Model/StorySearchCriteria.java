package tesfaye.venieri.software.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorySearchCriteria {
    private String title;
    private Long authorId;
    private boolean isPremium;
    private int page = 0;
    private int size = 10;
} 