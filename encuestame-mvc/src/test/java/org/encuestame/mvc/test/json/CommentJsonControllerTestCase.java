/*
 ************************************************************************************
 * Copyright (C) 2001-2011 encuestame: system online surveys Copyright (C) 2011
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.mvc.test.json;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import junit.framework.Assert;
import org.encuestame.mvc.controller.json.MethodJson;
import org.encuestame.mvc.test.config.AbstractJsonMvcUnitBeans;
import org.encuestame.persistence.domain.Comment;
import org.encuestame.persistence.domain.question.Question;
import org.encuestame.persistence.domain.tweetpoll.TweetPoll;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * Comment Json Controller TestCase.
 *
 * @author Morales, Diana Paola paolaATencuestame.org
 * @since August 17, 2011
 */
public class CommentJsonControllerTestCase extends AbstractJsonMvcUnitBeans {

    /** {@link TweetPoll} **/
    private TweetPoll tweetPoll;

    /** {@link Comment} **/
    private Comment comment;

    /** {@link Question} **/
    private Question question;

    @Before
    public void initJsonService(){
        this.question = createQuestion("Why the sky is blue?","html");
        this.tweetPoll = createTweetPollPublicated(true, true, new Date(), getSpringSecurityLoggedUserAccount(), this.question);
        this.comment = createDefaultTweetPollComment("My first comment", this.tweetPoll, getSpringSecurityLoggedUserAccount());
    }

    /**
     * Test get comments by unknown tweetPoll json.
     * @throws ServletException
     * @throws IOException
     */
    //@Test
    public void testGetCommentsbyUnknownTweetPoll() throws ServletException, IOException {
        initService("/api/survey/tweetpoll/comments.json", MethodJson.GET);
        setParameter("tweetPollId", "1");
        final JSONObject response = callJsonService();
        final String error = getErrorsMessage(response);
        Assert.assertEquals(error, "tweet poll invalid with this id 1");
    }

    /**
     * Test get comments by tweetPoll json.
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testGetCommentsbyTweetPoll() throws ServletException, IOException {
        initService("/api/survey/tweetpoll/comments.json", MethodJson.GET);
        setParameter("tweetPollId", this.tweetPoll.getTweetPollId().toString());
        final JSONObject response = callJsonService();
        final JSONObject success = getSucess(response);
        final JSONArray comments = (JSONArray) success.get("comments");
        Assert.assertEquals(comments.size(), 1);
    }

    /**
     * Test get comments by keyword json.
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testGetComments() throws ServletException, IOException{
        createDefaultTweetPollComment("My first comment", this.tweetPoll, getSpringSecurityLoggedUserAccount());
        createDefaultTweetPollComment("My Second comment", this.tweetPoll, getSpringSecurityLoggedUserAccount());
        createDefaultTweetPollComment("My Third comment", this.tweetPoll, getSpringSecurityLoggedUserAccount());
        flushIndexes();
        initService("/api/common/comment/search.json", MethodJson.GET);
        setParameter("keyword", "comment");
        setParameter("limit", "10");
        final JSONObject response = callJsonService();
        final JSONObject success = getSucess(response);
        final JSONArray comments = (JSONArray) success.get("comments");
        Assert.assertEquals(comments.size(), 4);
    }

    /**
     * Like vote comment json.
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testLikeVoteComment() throws ServletException, IOException{
        initService("/api/common/comment/like_vote.json", MethodJson.GET);
        setParameter("commentId", this.comment.getCommentId().toString());
        final JSONObject response = callJsonService();
        assertSuccessResponse(response);
    }

    /**
     * Dislike vote comment json.
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testDislikeVoteComment() throws ServletException, IOException{
        initService("/api/common/comment/dislike_vote.json", MethodJson.GET);
        setParameter("commentId", this.comment.getCommentId().toString());
        final JSONObject response = callJsonService();
        assertSuccessResponse(response);
    }

    /**
     * Test create comment json.
     * @throws ServletException
     * @throws IOException
     */
     @Test
     public void testCreateComment() throws ServletException, IOException{
         initService("/api/common/comment/create.json", MethodJson.POST);
         setParameter("comment", "My Comment");
         setParameter("tweetPollId", this.tweetPoll.getTweetPollId().toString());
         final JSONObject response = callJsonService();
         final JSONObject success = getSucess(response);
         final JSONObject dashboard = (JSONObject) success.get("comment");
         Assert.assertEquals(dashboard.get("comment").toString(), "My Comment");
        }
}