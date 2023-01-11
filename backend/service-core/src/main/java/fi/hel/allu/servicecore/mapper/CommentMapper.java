package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.Comment;
import fi.hel.allu.model.domain.CommentInterface;
import fi.hel.allu.search.domain.ApplicationES;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CommentMapper {

   public List<ApplicationES> populateComments(Map<Integer, List<Comment>> mappedComments, List<ApplicationES> applicationESList){
       for (ApplicationES applicationES : applicationESList) {
           if (mappedComments.containsKey(applicationES.getId())) {
               List<Comment> comments = mappedComments.get(applicationES.getId());
               applicationES.setNrOfComments(0);
               if (comments != null || !comments.isEmpty()) {
                   applicationES.setNrOfComments(comments.size());
               }
               applicationES.setLatestComment(getLatestComment(comments));
           }
       }
       return applicationESList;
   }

    public  <T extends CommentInterface> String getLatestComment(List<T> comments) {
        return Optional.ofNullable(comments)
                .flatMap(commentList -> commentList.stream().max(Comparator.comparing(CommentInterface::getCreateTime))
                        .map(comment -> comment.getText()))
                .orElse(null);
    }
}