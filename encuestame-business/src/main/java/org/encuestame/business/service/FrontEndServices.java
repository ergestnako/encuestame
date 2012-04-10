/**
 * 
 */
package org.encuestame.business.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.encuestame.core.service.AbstractBaseService;
import org.encuestame.core.service.imp.IFrontEndService;
import org.encuestame.core.service.imp.SecurityOperations;
import org.encuestame.core.util.ConvertDomainBean;
import org.encuestame.core.util.EnMeUtils;
import org.encuestame.persistence.domain.AccessRate;
import org.encuestame.persistence.domain.HashTag;
import org.encuestame.persistence.domain.HashTagRanking;
import org.encuestame.persistence.domain.Hit;
import org.encuestame.persistence.domain.security.UserAccount;
import org.encuestame.persistence.domain.survey.Poll;
import org.encuestame.persistence.domain.survey.Survey;
import org.encuestame.persistence.domain.tweetpoll.TweetPoll;
import org.encuestame.persistence.domain.tweetpoll.TweetPollSavedPublishedStatus;
import org.encuestame.persistence.exception.EnMeExpcetion;
import org.encuestame.persistence.exception.EnMeNoResultsFoundException;
import org.encuestame.persistence.exception.EnMeSearchException;
import org.encuestame.utils.DateUtil;
import org.encuestame.utils.RelativeTimeEnum;
import org.encuestame.utils.enums.SearchPeriods;
import org.encuestame.utils.enums.TypeSearchResult;
import org.encuestame.utils.json.HomeBean;
import org.encuestame.utils.json.LinksSocialBean;
import org.encuestame.utils.json.TweetPollBean;
import org.encuestame.utils.web.HashTagBean;
import org.encuestame.utils.web.PollBean;
import org.encuestame.utils.web.ProfileRatedTopBean;
import org.encuestame.utils.web.SurveyBean;
import org.encuestame.utils.web.stats.GenericStatsBean;
import org.encuestame.utils.web.stats.HashTagDetailStats;
import org.encuestame.utils.web.stats.HashTagRankingBean;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Front End Service.
 *
 * @author Picado, Juan juanATencuestame.org
 * @since Oct 17, 2010 11:29:38 AM
 */
@Service
public class FrontEndServices  extends AbstractBaseService implements IFrontEndService {

	 /** Front End Service Log. **/
    private Logger log = Logger.getLogger(this.getClass());

    /** Max Results. **/
    private final Integer MAX_RESULTS = 15;

    /** **/
    @Autowired
    private TweetPollService tweetPollService;

    /** {@link PollService} **/
    @Autowired
    private PollService pollService;

    /** {@link SurveyService} **/
    @Autowired
    private SurveyService surveyService;

    /** {@link SecurityOperations} **/
    @Autowired
    private SecurityOperations securityService;

    /**
     *
     */
    private HashTagDetailStats hashTagItemDetailedStats = new HashTagDetailStats();

    /**
     *
     */
    private List<HashTagDetailStats> tagStatsDetail = new ArrayList<HashTagDetailStats>();

    /**
     *
     */
    private Long counterItemsbyMonth = 0L;

    /**
     * Search Items By tweetPoll.
     *
     * @param maxResults
     *            limit of results to return.
     * @return result of the search.
     * @throws EnMeSearchException
     *             search exception.
     */
    public List<TweetPollBean> searchItemsByTweetPoll(final String period,
            final Integer start, Integer maxResults,
            final HttpServletRequest request) throws EnMeSearchException {
        final List<TweetPollBean> results = new ArrayList<TweetPollBean>();
        if (maxResults == null) {
            maxResults = this.MAX_RESULTS;
        }
        log.debug("Max Results: " + maxResults);
        log.debug("Period Results: " + period);
        final List<TweetPoll> items = new ArrayList<TweetPoll>();
        if (period == null) {
            throw new EnMeSearchException("search params required.");
        } else {
            final SearchPeriods periodSelected = SearchPeriods
                    .getPeriodString(period);
            if (periodSelected.equals(SearchPeriods.TWENTYFOURHOURS)) {
                items.addAll(getFrontEndDao().getTweetPollFrontEndLast24(start,
                        maxResults));
            } else if (periodSelected.equals(SearchPeriods.SEVENDAYS)) {
                items.addAll(getFrontEndDao().getTweetPollFrontEndLast7Days(
                        start, maxResults));
            } else if (periodSelected.equals(SearchPeriods.THIRTYDAYS)) {
                items.addAll(getFrontEndDao().getTweetPollFrontEndLast30Days(
                        start, maxResults));
            } else if (periodSelected.equals(SearchPeriods.ALLTIME)) {
                items.addAll(getFrontEndDao().getTweetPollFrontEndAllTime(
                        start, maxResults));
            }
            results.addAll(ConvertDomainBean.convertListToTweetPollBean(items));
            for (TweetPollBean tweetPoll : results) {
                // log.debug("Iterate Home TweetPoll id: "+tweetPoll.getId());
                // log.debug("Iterate Home Tweetpoll Hashtag Size: "+tweetPoll.getHashTags().size());
                tweetPoll = convertTweetPollRelativeTime(tweetPoll, request);
                tweetPoll.setTotalComments(this.getTotalCommentsbyType(
                        tweetPoll.getId(), TypeSearchResult.TWEETPOLL));
            }

        }
        log.debug("Search Items by TweetPoll: " + results.size());
        return results;
    }

    /**
     *
     */
    public List<SurveyBean> searchItemsBySurvey(final String period,
            final Integer start, Integer maxResults,
            final HttpServletRequest request) throws EnMeSearchException {
        final List<SurveyBean> results = new ArrayList<SurveyBean>();
        if (maxResults == null) {
            maxResults = this.MAX_RESULTS;
        }
        log.debug("Max Results " + maxResults);
        final List<Survey> items = new ArrayList<Survey>();
        if (period == null) {
            throw new EnMeSearchException("search params required.");
        } else {
            final SearchPeriods periodSelected = SearchPeriods
                    .getPeriodString(period);
            if (periodSelected.equals(SearchPeriods.TWENTYFOURHOURS)) {
                items.addAll(getFrontEndDao().getSurveyFrontEndLast24(start,
                        maxResults));
            } else if (periodSelected.equals(SearchPeriods.SEVENDAYS)) {
                items.addAll(getFrontEndDao().getSurveyFrontEndLast7Days(start,
                        maxResults));
            } else if (periodSelected.equals(SearchPeriods.THIRTYDAYS)) {
                items.addAll(getFrontEndDao().getSurveyFrontEndLast30Days(
                        start, maxResults));
            } else if (periodSelected.equals(SearchPeriods.ALLTIME)) {
                items.addAll(getFrontEndDao().getSurveyFrontEndAllTime(start,
                        maxResults));
            }
            log.debug("TweetPoll " + items.size());
            results.addAll(ConvertDomainBean.convertListSurveyToBean(items));
            for (SurveyBean surveyBean : results) {
                surveyBean.setTotalComments(this.getTotalCommentsbyType(
                        surveyBean.getSid(), TypeSearchResult.SURVEY));
            }
        }
        return results;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#getFrontEndItems(java
     * .lang.String, java.lang.Integer, java.lang.Integer,
     * javax.servlet.http.HttpServletRequest)
     */
    public List<HomeBean> getFrontEndItems(final String period,
            final Integer start, Integer maxResults,
            final HttpServletRequest request) throws EnMeSearchException {
        // Sorted list based comparable interface
        final List<HomeBean> allItems = new ArrayList<HomeBean>();
        final List<TweetPollBean> tweetPollItems = this.searchItemsByTweetPoll(
                period, start, maxResults, request);
        log.debug("FrontEnd TweetPoll items size  :" + tweetPollItems.size());
        allItems.addAll(ConvertDomainBean
                .convertTweetPollListToHomeBean(tweetPollItems));
        final List<PollBean> pollItems = this.searchItemsByPoll(period, start,
                maxResults);
        log.debug("FrontEnd Poll items size  :" + pollItems.size());
        allItems.addAll(ConvertDomainBean.convertPollListToHomeBean(pollItems));
        final List<SurveyBean> surveyItems = this.searchItemsBySurvey(period,
                start, maxResults, request);
        log.debug("FrontEnd Survey items size  :" + surveyItems.size());
        allItems.addAll(ConvertDomainBean
                .convertSurveyListToHomeBean(surveyItems));
        log.debug("Home bean list size :" + allItems.size());
        Collections.sort(allItems);
        return allItems;
    }

    /**
     * Search items by poll.
     *
     * @param period
     * @param maxResults
     * @return
     * @throws EnMeSearchException
     */
    public List<PollBean> searchItemsByPoll(final String period,
            final Integer start, Integer maxResults) throws EnMeSearchException {
        final List<PollBean> results = new ArrayList<PollBean>();
        log.debug("searchItemsByPoll period " + period);
        log.debug("searchItemsByPoll start " + period);
        log.debug("searchItemsByPoll maxResults " + maxResults);
        //avoid null values
        maxResults = maxResults == null ? this.MAX_RESULTS : maxResults;       
        final List<Poll> items = new ArrayList<Poll>();
        if (period == null) {
            throw new EnMeSearchException("search params required");
        } else {
            final SearchPeriods periodSelected = SearchPeriods
                    .getPeriodString(period);
            if (periodSelected.equals(SearchPeriods.TWENTYFOURHOURS)) {
                items.addAll(getFrontEndDao().getPollFrontEndLast24(start,
                        maxResults));
            } else if (periodSelected.equals(SearchPeriods.SEVENDAYS)) {
                items.addAll(getFrontEndDao().getPollFrontEndLast7Days(start,
                        maxResults));
            } else if (periodSelected.equals(SearchPeriods.THIRTYDAYS)) {
                items.addAll(getFrontEndDao().getPollFrontEndLast30Days(start,
                        maxResults));
            } else if (periodSelected.equals(SearchPeriods.ALLTIME)) {
                items.addAll(getFrontEndDao().getPollFrontEndAllTime(start,
                        maxResults));
            }
            log.debug("Poll:--> " + items.size());
            results.addAll(ConvertDomainBean.convertListToPollBean((items)));
            for (PollBean pollbean : results) {
                pollbean.setTotalComments(this.getTotalCommentsbyType(
                        pollbean.getId(), TypeSearchResult.POLL));
            }
        }
        log.debug("Poll results:--> " + results.size());
        return results;
    }

    public void search() {

    }

    /**
     * Get hashTags
     *
     * @param maxResults
     *            the max results to display
     * @param start
     *            to pagination propose.
     * @return List of {@link HashTagBean}
     */
    public List<HashTagBean> getHashTags(Integer maxResults,
            final Integer start, final String tagCriteria) {
        final List<HashTagBean> hashBean = new ArrayList<HashTagBean>();
        if (maxResults == null) {
            maxResults = this.MAX_RESULTS;
        }
        final List<HashTag> tags = getHashTagDao().getHashTags(maxResults,
                start, tagCriteria);
        hashBean.addAll(ConvertDomainBean.convertListHashTagsToBean(tags));
        return hashBean;
    }

    /**
     * Get hashTag item.
     *
     * @param tagName
     * @return
     * @throws EnMeNoResultsFoundException
     */
    public HashTag getHashTagItem(final String tagName)
            throws EnMeNoResultsFoundException {
        final HashTag tag = getHashTagDao().getHashTagByName(tagName);
        if (tag == null) {
            throw new EnMeNoResultsFoundException("hashtag not found");
        }
        return tag;
    }

    /**
     * Get TweetPolls by hashTag id.
     *
     * @param hashTagId
     * @param limit
     * @return
     */
    public List<TweetPollBean> getTweetPollsbyHashTagName(final String tagName,
            final Integer initResults, final Integer limit,
            final String filter, final HttpServletRequest request) {
        final List<TweetPoll> tweetPolls = getTweetPollDao()
                .getTweetpollByHashTagName(tagName, initResults, limit,
                        TypeSearchResult.getTypeSearchResult(filter));
        log.debug("TweetPoll by HashTagId total size ---> " + tweetPolls.size());
        final List<TweetPollBean> tweetPollBean = ConvertDomainBean
                .convertListToTweetPollBean(tweetPolls);
        for (TweetPollBean tweetPoll : tweetPollBean) {
            tweetPoll = convertTweetPollRelativeTime(tweetPoll, request);
        }
        return tweetPollBean;
    }

    public List<HomeBean> searchLastPublicationsbyHashTag(
            final HashTag hashTag, final String keyword,
            final Integer initResults, final Integer limit,
            final String filter, final HttpServletRequest request) {
        final List<HomeBean> allItems = new ArrayList<HomeBean>();
        final List<TweetPollBean> tweetPollItems = this
                .getTweetPollsbyHashTagName(hashTag.getHashTag(), initResults,
                        limit, filter, request);
        log.debug("FrontEnd TweetPoll items size  :" + tweetPollItems.size());
        allItems.addAll(ConvertDomainBean
                .convertTweetPollListToHomeBean(tweetPollItems));
        Collections.sort(allItems);
        return allItems;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#checkPreviousHit(java
     * .lang.String, java.lang.Long, java.lang.String)
     */
    public Boolean checkPreviousHit(final String ipAddress, final Long id,
            final TypeSearchResult searchHitby) {
        boolean hit = false;
        final List<Hit> hitList = getFrontEndDao().getHitsByIpAndType(
                ipAddress, id, searchHitby);
        try {
            if (hitList.size() == 1) {
                if (hitList.get(0).getIpAddress().equals(ipAddress)) {
                    hit = true;
                }
            } else if (hitList.size() > 1) {
                log.error("List cant'be greater than one");
            }
        } catch (Exception e) {
            log.error(e);
        }
        return hit;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#registerHit(org.encuestame
     * .persistence.domain.tweetpoll.TweetPoll,
     * org.encuestame.persistence.domain.survey.Poll,
     * org.encuestame.persistence.domain.survey.Survey,
     * org.encuestame.persistence.domain.HashTag, java.lang.String)
     */
    public Boolean registerHit(final TweetPoll tweetPoll, final Poll poll,
            final Survey survey, final HashTag tag, final String ip)
            throws EnMeNoResultsFoundException {
        final Hit hit;
        Long hitCount = 1L;
        Boolean register = false;
        // HashTag
        if (ip != null) {
            if (tag != null) {
                hit = this.newHashTagHit(tag, ip);
                hitCount = tag.getHits() == null ? 0L : tag.getHits()
                        + hitCount;
                tag.setHits(hitCount);
                getFrontEndDao().saveOrUpdate(tag);
                register = true;
            } else if (tweetPoll != null) {
                hit = this.newTweetPollHit(tweetPoll, ip);
                hitCount = tweetPoll.getHits() + hitCount;
                tweetPoll.setHits(hitCount);
                getFrontEndDao().saveOrUpdate(tweetPoll);
                register = true;
            } else if (poll != null) {
                hit = this.newPollHit(poll, ip);
                hitCount = poll.getHits() + hitCount;
                poll.setHits(hitCount);
                getFrontEndDao().saveOrUpdate(poll);
                register = true;
            } else if (survey != null) {
                hit = this.newSurveyHit(survey, ip);
                hitCount = survey.getHits() + hitCount;
                survey.setHits(hitCount);
                getFrontEndDao().saveOrUpdate(survey);
                register = true;
            }
        }
        return register;
    }

    /**
     * New hit item.
     *
     * @param tweetPoll
     * @param poll
     * @param survey
     * @param tag
     * @param ipAddress
     * @return
     */

    @Transactional(readOnly = false)
    private Hit newHitItem(final TweetPoll tweetPoll, final Poll poll,
            final Survey survey, final HashTag tag, final String ipAddress) {
        final Hit hitItem = new Hit();
        hitItem.setHitDate(Calendar.getInstance().getTime());
        hitItem.setHashTag(tag);
        hitItem.setIpAddress(ipAddress);
        hitItem.setTweetPoll(tweetPoll);
        hitItem.setPoll(poll);
        hitItem.setSurvey(survey);
        getFrontEndDao().saveOrUpdate(hitItem);
        return hitItem;
    }

    /**
     * New tweet poll hit item.
     *
     * @param tweetPoll
     * @param ipAddress
     * @return
     */
    private Hit newTweetPollHit(final TweetPoll tweetPoll,
            final String ipAddress) {
        return this.newHitItem(tweetPoll, null, null, null, ipAddress);
    }

    /**
     * New poll hit item.
     *
     * @param poll
     * @param ipAddress
     * @return
     */
    private Hit newPollHit(final Poll poll, final String ipAddress) {
        return this.newHitItem(null, poll, null, null, ipAddress);
    }

    /**
     * New hash tag hit item.
     *
     * @param tag
     * @param ipAddress
     * @return
     */
    private Hit newHashTagHit(final HashTag tag, final String ipAddress) {
        return this.newHitItem(null, null, null, tag, ipAddress);
    }

    /**
     * New survey hit item.
     *
     * @param survey
     * @param ipAddress
     * @return
     */
    private Hit newSurveyHit(final Survey survey, final String ipAddress) {
        return this.newHitItem(null, null, survey, null, ipAddress);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#registerAccessRate(org
     * .encuestame.persistence.domain.TypeSearchResult, java.lang.Long,
     * java.lang.String, java.lang.Boolean)
     */
    public AccessRate registerAccessRate(final TypeSearchResult type,
            final Long itemId, final String ipAddress, final Boolean rate)
            throws EnMeExpcetion {
        AccessRate recordAccessRate = new AccessRate();
        if (ipAddress != null) {
            if (type.equals(TypeSearchResult.TWEETPOLL)) {
                // Find tweetPoll by itemId.
                final TweetPoll tweetPoll = this.getTweetPoll(itemId);
                Assert.assertNotNull(tweetPoll);
                // Check if exist a previous tweetpoll access record.
                recordAccessRate = this.checkExistTweetPollPreviousRecord(
                        tweetPoll, ipAddress, rate);
            }
            // Poll Acces rate item.
            if (type.equals(TypeSearchResult.POLL)) {
                // Find poll by itemId.
                final Poll poll = this.getPoll(itemId);
                Assert.assertNotNull(poll);
                // Check if exist a previous poll access record.
                recordAccessRate = this.checkExistPollPreviousRecord(poll,
                        ipAddress, rate);
            }
            // Survey Access rate item.
            if (type.equals(TypeSearchResult.SURVEY)) {
                // Find survey by itemId.
                final Survey survey = this.getSurvey(itemId);
                Assert.assertNotNull(survey);
                // Check if exist a previous survey access record.
                recordAccessRate = this.checkExistSurveyPreviousRecord(survey,
                        ipAddress, rate);
            }
        }
        return recordAccessRate;
    }

    /**
     * Check exist tweetPoll previous record.
     *
     * @param tpoll
     * @param ipAddress
     * @param option
     * @throws EnMeExpcetion
     */
    private AccessRate checkExistTweetPollPreviousRecord(final TweetPoll tpoll,
            final String ipAddress, final Boolean option) throws EnMeExpcetion {
        // Search record by tweetPpll in access Rate domain.
        List<AccessRate> rateList = this.getAccessRateItem(ipAddress,
                tpoll.getTweetPollId(), TypeSearchResult.TWEETPOLL);
        final AccessRate accessRate;
        if (rateList.size() > 1) {
            throw new EnMeExpcetion(
                    "Access rate list found coudn't be greater than one ");
        } else if (rateList.size() == 1) {
            // Get first element from access rate list
            accessRate = rateList.get(0);
            // Check if the option selected is the same that you have registered
            if (accessRate.getRate() == option) {
                log.warn("The option was previously selected "
                        + accessRate.getRate());
            } else {
                // We proceed to update the record in the table access Rate.
                accessRate.setRate(option);
                // Update the value in the fields of TweetPoll
                this.setTweetPollSocialOption(option, tpoll);
                // Save access rate record.
                getFrontEndDao().saveOrUpdate(accessRate);
            }

        } else {
            // Otherwise, create access rate record.
            accessRate = this.newTweetPollAccessRate(tpoll, ipAddress, option);
            // update tweetPoll record.
            this.setTweetPollSocialOption(option, tpoll);
        }
        return accessRate;
    }

    /**
     * Check exist Poll previous record.
     *
     * @param poll
     * @param ipAddress
     * @param option
     * @return
     * @throws EnMeExpcetion
     */
    private AccessRate checkExistPollPreviousRecord(final Poll poll,
            final String ipAddress, final Boolean option) throws EnMeExpcetion {
        // Search record by poll in access Rate domain.
        List<AccessRate> rateList = this.getAccessRateItem(ipAddress,
                poll.getPollId(), TypeSearchResult.POLL);
        final AccessRate accessRate;
        if (rateList.size() > 1) {
            throw new EnMeExpcetion(
                    "Access rate list found coudn't be greater than one ");
        } else if (rateList.size() == 1) {
            // Get first element from access rate list
            accessRate = rateList.get(0);
            // Check if the option selected is the same that you have registered
            if (accessRate.getRate() == option) {
                log.warn("The option was previously selected "
                        + accessRate.getRate());
            } else {
                // We proceed to update the record in the table access Rate.
                accessRate.setRate(option);
                // Update the value in the fields of TweetPoll
                this.setPollSocialOption(option, poll);
                // Save access rate record.
                getFrontEndDao().saveOrUpdate(accessRate);
            }

        } else {
            // Otherwise, create access rate record.
            accessRate = this.newPollAccessRate(poll, ipAddress, option);
            // update poll record.
            this.setPollSocialOption(option, poll);
        }
        return accessRate;
    }

    /**
     * Check exist Survey previous record.
     *
     * @param survey
     * @param ipAddress
     * @param option
     * @return
     * @throws EnMeExpcetion
     */
    private AccessRate checkExistSurveyPreviousRecord(final Survey survey,
            final String ipAddress, final Boolean option) throws EnMeExpcetion {
        // Search record by survey in access Rate domain.
        List<AccessRate> rateList = this.getAccessRateItem(ipAddress,
                survey.getSid(), TypeSearchResult.SURVEY);
        final AccessRate accessRate;
        if (rateList.size() > 1) {
            throw new EnMeExpcetion(
                    "Access rate list found coudn't be greater than one ");
        } else if (rateList.size() == 1) {
            // Get first element from access rate list
            accessRate = rateList.get(0);
            // Check if the option selected is the same that you have registered
            if (accessRate.getRate() == option) {
                log.warn("The option was previously selected "
                        + accessRate.getRate());
            } else {
                // We proceed to update the record in the table access Rate.
                accessRate.setRate(option);
                // Update the value in the fields of survey
                this.setSurveySocialOption(option, survey);
                // Save access rate record.
                getFrontEndDao().saveOrUpdate(accessRate);
            }

        } else {
            // Otherwise, create access rate record.
            accessRate = this.newSurveyAccessRate(survey, ipAddress, option);
            // update poll record.
            this.setSurveySocialOption(option, survey);
        }
        return accessRate;
    }

    /**
     * Set tweetpoll social options.
     *
     * @param socialOption
     * @param tpoll
     * @return
     */
    private TweetPoll setTweetPollSocialOption(final Boolean socialOption,
            final TweetPoll tpoll) {
        long valueSocialVote = 1L;
        long optionValue;
        // If the user has voted like.
        if (socialOption) {
            valueSocialVote = tpoll.getLikeVote() + valueSocialVote;
            tpoll.setLikeVote(valueSocialVote);
            optionValue = tpoll.getLikeVote() - valueSocialVote;
            tpoll.setDislikeVote(tpoll.getDislikeVote() == 0 ? 0 : optionValue);
            getTweetPollDao().saveOrUpdate(tpoll);
        } else {
            valueSocialVote = tpoll.getDislikeVote() + valueSocialVote;
            optionValue = tpoll.getLikeVote() - valueSocialVote;
            tpoll.setLikeVote(tpoll.getLikeVote() == 0 ? 0 : optionValue);
            tpoll.setDislikeVote(valueSocialVote);
            getTweetPollDao().saveOrUpdate(tpoll);
        }
        return tpoll;
    }

    /**
     * Set Poll social option.
     *
     * @param socialOption
     * @param poll
     * @return
     */
    private Poll setPollSocialOption(final Boolean socialOption, final Poll poll) {
        long valueSocialVote = 1L;
        long optionValue;
        // If the user has voted like.
        if (socialOption) {
            valueSocialVote = poll.getLikeVote() + valueSocialVote;
            poll.setLikeVote(valueSocialVote);
            optionValue = poll.getLikeVote() - valueSocialVote;
            poll.setDislikeVote(poll.getDislikeVote() == 0 ? 0 : optionValue);
            getTweetPollDao().saveOrUpdate(poll);
        } else {
            valueSocialVote = poll.getDislikeVote() + valueSocialVote;
            optionValue = poll.getLikeVote() - valueSocialVote;
            poll.setLikeVote(poll.getLikeVote() == 0 ? 0 : optionValue);
            poll.setDislikeVote(valueSocialVote);
            getTweetPollDao().saveOrUpdate(poll);
        }
        return poll;
    }

    /**
     * Set Survey social option.
     *
     * @param socialOption
     * @param survey
     * @return
     */
    private Survey setSurveySocialOption(final Boolean socialOption,
            final Survey survey) {
        long valueSocialVote = 1L;
        long optionValue;
        // If the user has voted like.
        if (socialOption) {
            valueSocialVote = survey.getLikeVote() + valueSocialVote;
            survey.setLikeVote(valueSocialVote);
            optionValue = survey.getLikeVote() - valueSocialVote;
            survey.setDislikeVote(survey.getDislikeVote() == 0 ? 0
                    : optionValue);
            getTweetPollDao().saveOrUpdate(survey);
        } else {
            valueSocialVote = survey.getDislikeVote() + valueSocialVote;
            optionValue = survey.getLikeVote() - valueSocialVote;
            survey.setLikeVote(survey.getLikeVote() == 0 ? 0 : optionValue);
            survey.setDislikeVote(valueSocialVote);
            getTweetPollDao().saveOrUpdate(survey);
        }
        return survey;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#getAccessRateItem(java
     * .lang.String, java.lang.Long,
     * org.encuestame.persistence.domain.TypeSearchResult)
     */
    private List<AccessRate> getAccessRateItem(final String ipAddress,
            final Long itemId, final TypeSearchResult searchby)
            throws EnMeExpcetion {
        final List<AccessRate> itemAccessList = getFrontEndDao()
                .getAccessRatebyItem(ipAddress, itemId, searchby);
        return itemAccessList;
    }

    /**
     * New access rate item.
     *
     * @param tweetPoll
     * @param poll
     * @param survey
     * @param ipAddress
     * @param rate
     * @return
     */
    @Transactional(readOnly = false)
    private AccessRate newAccessRateItem(final TweetPoll tweetPoll,
            final Poll poll, final Survey survey, final String ipAddress,
            final Boolean rate) {
        final AccessRate itemRate = new AccessRate();
        itemRate.setTweetPoll(tweetPoll);
        itemRate.setPoll(poll);
        itemRate.setSurvey(survey);
        itemRate.setRate(rate);
        itemRate.setUser(null);
        itemRate.setIpAddress(ipAddress);
        getTweetPollDao().saveOrUpdate(itemRate);
        return itemRate;
    }

    /**
     * New TweetPoll access rate.
     *
     * @param tweetPoll
     * @param ipAddress
     * @param rate
     * @return
     */
    private AccessRate newTweetPollAccessRate(final TweetPoll tweetPoll,
            final String ipAddress, final Boolean rate) {
        return this.newAccessRateItem(tweetPoll, null, null, ipAddress, rate);
    }

    /**
     * New Poll access rate.
     *
     * @param poll
     * @param ipAddress
     * @param rate
     * @return
     */
    private AccessRate newPollAccessRate(final Poll poll,
            final String ipAddress, final Boolean rate) {
        return this.newAccessRateItem(null, poll, null, ipAddress, rate);
    }

    /**
     * New Survey access rate.
     *
     * @param survey
     * @param ipAddress
     * @param rate
     * @return
     */
    private AccessRate newSurveyAccessRate(final Survey survey,
            final String ipAddress, final Boolean rate) {
        return this.newAccessRateItem(null, null, survey, ipAddress, rate);
    }

    /**
     *
     * @param id
     * @return
     * @throws EnMeNoResultsFoundException
     */
    private TweetPoll getTweetPoll(final Long id)
            throws EnMeNoResultsFoundException {
        return getTweetPollService().getTweetPollById(id);
    }

    private Integer getSocialAccountsLinksByItem(final TweetPoll tpoll,
            final Survey survey, final Poll poll,
            final TypeSearchResult itemType) {
        final List<TweetPollSavedPublishedStatus> totalAccounts = getTweetPollDao()
                .getLinksByTweetPoll(tpoll, survey, poll, itemType);
        return totalAccounts.size();

    }

    /**
     * Get Relevance value by item.
     *
     * @param likeVote
     * @param dislikeVote
     * @param hits
     * @param totalComments
     * @param totalSocialAccounts
     * @param totalNumberVotes
     * @param totalHashTagHits
     * @return
     */
    private long getRelevanceValue(final long likeVote, final long dislikeVote,
            final long hits, final long totalComments,
            final long totalSocialAccounts, final long totalNumberVotes,
            final long totalHashTagHits) {
        final long relevanceValue = EnMeUtils.calculateRelevance(likeVote,
                dislikeVote, hits, totalComments, totalSocialAccounts,
                totalNumberVotes, totalHashTagHits);
        log.info("*******************************");
        log.info("******* Resume of Process *****");
        log.info("-------------------------------");
        log.info("|  Total like votes : " + likeVote + "            |");
        log.info("|  Total dislike votes : " + dislikeVote + "            |");
        log.info("|  Total hits : " + hits + "            |");
        log.info("|  Total Comments : " + totalComments + "            |");
        log.info("|  Total Social Network : " + totalSocialAccounts
                + "            |");
        log.info("|  Total Votes : " + totalNumberVotes + "            |");
        log.info("|  Total HashTag hits : " + totalHashTagHits
                + "            |");
        log.info("-------------------------------");
        log.info("*******************************");
        log.info("************ Finished Start Relevance calculate job **************");
        return relevanceValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.encuestame.core.service.imp.IFrontEndService#
     * processItemstoCalculateRelevance(java.util.List, java.util.List,
     * java.util.List, java.util.Calendar, java.util.Calendar)
     */
    public void processItemstoCalculateRelevance(
            final List<TweetPoll> tweetPollList, final List<Poll> pollList,
            final List<Survey> surveyList, final Calendar datebefore,
            final Calendar todayDate) {
        long likeVote;
        long dislikeVote;
        long hits;
        long relevance;
        long comments;
        long socialAccounts;
        long numberVotes;
        long hashTagHits;
        for (TweetPoll tweetPoll : tweetPollList) {
            likeVote = tweetPoll.getLikeVote() == null ? 0 : tweetPoll
                    .getLikeVote();
            dislikeVote = tweetPoll.getDislikeVote() == null ? 0 : tweetPoll
                    .getDislikeVote();
            hits = tweetPoll.getHits() == null ? 0 : tweetPoll.getHits();
            // final Long userId = tweetPoll.getEditorOwner().getUid();
            socialAccounts = this.getSocialAccountsLinksByItem(tweetPoll, null,
                    null, TypeSearchResult.TWEETPOLL);
            numberVotes = tweetPoll.getNumbervotes();
            comments = getTotalCommentsbyType(tweetPoll.getTweetPollId(),
                    TypeSearchResult.TWEETPOLL);
            log.debug("Total comments by TweetPoll ---->" + comments);
            hashTagHits = this.getHashTagHits(tweetPoll.getTweetPollId(),
                    TypeSearchResult.HASHTAG);
            relevance = this.getRelevanceValue(likeVote, dislikeVote, hits,
                    comments, socialAccounts, numberVotes, hashTagHits);
            tweetPoll.setRelevance(relevance);
            getTweetPollDao().saveOrUpdate(tweetPoll);
        }

        for (Poll poll : pollList) {
            likeVote = poll.getLikeVote() == null ? 0 : poll.getLikeVote();
            dislikeVote = poll.getDislikeVote() == null ? 0 : poll
                    .getDislikeVote();
            hits = poll.getHits() == null ? 0 : poll.getHits();
            socialAccounts = this.getSocialAccountsLinksByItem(null, null,
                    poll, TypeSearchResult.POLL);
            numberVotes = poll.getNumbervotes();
            comments = getTotalCommentsbyType(poll.getPollId(),
                    TypeSearchResult.POLL);
            log.debug("Total Comments by Poll ---->" + comments);
            hashTagHits = this.getHashTagHits(poll.getPollId(),
                    TypeSearchResult.HASHTAG);
            relevance = this.getRelevanceValue(likeVote, dislikeVote, hits,
                    comments, socialAccounts, numberVotes, hashTagHits);
            poll.setRelevance(relevance);
            getPollDao().saveOrUpdate(poll);
        }

    }

    /**
     * Get total hash tag hits.
     *
     * @param id
     * @param filterby
     * @return
     */
    private Long getHashTagHits(final Long id, final TypeSearchResult filterby) {
        final Long totalHashTagHits = getFrontEndDao().getTotalHitsbyType(id,
                TypeSearchResult.HASHTAG);
        return totalHashTagHits;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#getHashTagHitsbyName
     * (java.lang.String, org.encuestame.utils.enums.TypeSearchResult)
     */
    public Long getHashTagHitsbyName(final String tagName,
            final TypeSearchResult filterBy) {
        final HashTag tag = getHashTagDao().getHashTagByName(tagName);
        final Long hits = this.getHashTagHits(tag.getHashTagId(),
                TypeSearchResult.HASHTAG);
        return hits;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#getTotalUsageByHashTag
     * (java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.String)
     */
    public Long getTotalUsageByHashTag(final String tagName,
            final Integer initResults, final Integer maxResults,
            final TypeSearchResult filter) {
        // Validate if tag belongs to hashtag and filter isn't empty.
        Long totalUsagebyHashTag = 0L;
        final HashTag tag = getHashTagDao().getHashTagByName(tagName);
        if (tag != null) {

            final List<TweetPoll> tweetsbyTag = this.getTweetPollsByHashTag(
                    tagName, initResults, maxResults, filter);
            final int totatTweetPolls = tweetsbyTag.size();
            final List<Poll> pollsbyTag = this.getPollsByHashTag(tagName,
                    initResults, maxResults, filter);
            final int totalPolls = pollsbyTag.size();
            final List<Survey> surveysbyTag = this.getSurveysByHashTag(tagName,
                    initResults, maxResults, filter);
            final int totalSurveys = surveysbyTag.size();
            totalUsagebyHashTag = (long) (totatTweetPolls + totalPolls + totalSurveys);

        }
        return totalUsagebyHashTag;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#getSocialNetworkUseByHashTag
     * (java.lang.String, java.lang.Integer, java.lang.Integer,
     * java.lang.String)
     */

    public Long getSocialNetworkUseByHashTag(final String tagName,
            final Integer initResults, final Integer maxResults) {
        // 1- Get tweetPoll, Polls o Survey
        Long linksbyTweetPoll = 0L;
        Long linksbyPoll = 0L;
        Long totalSocialLinks = 0L;

        linksbyTweetPoll = this.getTweetPollSocialNetworkLinksbyTag(tagName,
                initResults, maxResults, TypeSearchResult.TWEETPOLL);
        linksbyPoll = this.getPollsSocialNetworkLinksByTag(tagName,
                initResults, maxResults, TypeSearchResult.POLL);
        totalSocialLinks = linksbyTweetPoll + linksbyPoll;
        return totalSocialLinks;
    }

    /**
     * Get polls social network links by tag.
     *
     * @param tagName
     * @param initResults
     * @param maxResults
     * @param filter
     * @return
     */
    private Long getPollsSocialNetworkLinksByTag(final String tagName,
            final Integer initResults, final Integer maxResults,
            final TypeSearchResult filter) {
        Long linksbyItem = 0L;
        Long totalLinksByPoll = 0L;

        final List<Poll> polls = this.getPollsByHashTag(tagName, initResults,
                maxResults, filter);
        for (Poll poll : polls) {
            linksbyItem = getTweetPollDao().getSocialLinksByType(null, null,
                    poll, TypeSearchResult.POLL);
            totalLinksByPoll = totalLinksByPoll + linksbyItem;
        }
        return totalLinksByPoll;
    }

    /**
     * Get tweetPolls social network links by tag
     *
     * @param tagName
     * @param initResults
     * @param maxResults
     * @param filter
     * @return
     */
    private Long getTweetPollSocialNetworkLinksbyTag(final String tagName,
            final Integer initResults, final Integer maxResults,
            final TypeSearchResult filter) {
        Long linksbyItem = 0L;
        Long totalLinksByTweetPoll = 0L;

        final List<TweetPoll> tp = this.getTweetPollsByHashTag(tagName,
                initResults, maxResults, filter);

        for (TweetPoll tweetPoll : tp) {
            // Get total value by links
            linksbyItem = getTweetPollDao().getSocialLinksByType(tweetPoll,
                    null, null, TypeSearchResult.TWEETPOLL);
            totalLinksByTweetPoll = totalLinksByTweetPoll + linksbyItem;
        }

        return totalLinksByTweetPoll;
    }

    public List<HashTagDetailStats> getTweetPollSocialNetworkLinksbyTagAndDateRange(
            final String tagName, final Integer initResults,
            final Integer maxResults, final TypeSearchResult filter,
            final Integer period) {
        Long linksbyItem = 0L;

        final List<HashTagDetailStats> tpollListByLink = new ArrayList<HashTagDetailStats>();
        List<HashTagDetailStats> hashTagDetailedStatisticsListbyTweetPoll = new ArrayList<HashTagDetailStats>();
        HashTagDetailStats detailItem = new HashTagDetailStats();

        final List<TweetPoll> tpolls = this.getTweetPollsByHashTag(tagName,
                initResults, maxResults, filter);
        int monthValue = 0;

        for (TweetPoll tweetPoll : tpolls) {
            // Get total value by links
            linksbyItem = getTweetPollDao().getSocialLinksByTypeAndDateRange(
                    tweetPoll, null, null, filter, period, initResults,
                    initResults);

            DateTime dt = new DateTime(tweetPoll.getCreateDate());
            monthValue = dt.getMonthOfYear();

            if (linksbyItem > 0) {
                detailItem = createTagDetailsStats(String.valueOf(monthValue),
                        linksbyItem);
                tpollListByLink.add(detailItem);
            }
        }
        hashTagDetailedStatisticsListbyTweetPoll = this
                .getHashTagStatsDetailedtList(tpollListByLink);
        return hashTagDetailedStatisticsListbyTweetPoll;
    }
    /**
     * Get surveys by HashTag.
     *
     * @param tagName
     * @param initResults
     * @param maxResults
     * @param filter
     * @return
     */
    private List<Survey> getSurveysByHashTag(final String tagName,
            final Integer initResults, final Integer maxResults,
            final TypeSearchResult filter) {
        final List<Survey> surveysByTag = getSurveyDaoImp()
                .getSurveysByHashTagName(tagName, initResults, maxResults,
                        filter);
        return surveysByTag;
    }

    /**
     * Get Polls by HashTag
     *
     * @param tagName
     * @param initResults
     * @param maxResults
     * @param filter
     * @return
     */
    private List<Poll> getPollsByHashTag(final String tagName,
            final Integer initResults, final Integer maxResults,
            final TypeSearchResult filter) {
        final List<Poll> pollsByTag = getPollDao().getPollByHashTagName(
                tagName, initResults, maxResults, filter);
        return pollsByTag;
    }

    /**
     * Get TweetPolls by hashTag.
     *
     * @param tagId
     * @param initResults
     * @param maxResults
     * @param filter
     * @return
     */
    private List<TweetPoll> getTweetPollsByHashTag(final String tagName,
            final Integer initResults, final Integer maxResults,
            final TypeSearchResult filter) {
        final List<TweetPoll> tweetsbyTag = getTweetPollDao()
                .getTweetpollByHashTagName(tagName, initResults, maxResults,
                        filter);
        return tweetsbyTag;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#getHashTagUsedOnItemsVoted
     * (java.lang.String)
     */
    public Long getHashTagUsedOnItemsVoted(final String tagName,
            final Integer initResults, final Integer maxResults) {
        Long totalVotesbyTweetPoll = 0L;
        Long total = 0L;
        final List<TweetPoll> tp = this.getTweetPollsByHashTag(tagName, 0, 100,
                TypeSearchResult.HASHTAG);
        for (TweetPoll tweetPoll : tp) {
            totalVotesbyTweetPoll = getTweetPollDao()
                    .getTotalVotesByTweetPollId(tweetPoll.getTweetPollId());
            total = total + totalVotesbyTweetPoll;
        }
        log.debug("Total HashTag used by Tweetpoll voted: " + total);
        return total;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#getHashTagRanking(java
     * .lang.String)
     */
    public List<HashTagRankingBean> getHashTagRanking(final String tagName) {

        List<HashTagRanking> hashTagRankingList = getHashTagDao()
                .getHashTagRankStats();
        final Integer value = 1;
        Integer position = 0;
        final List<HashTagRankingBean> tagRankingBeanList = new ArrayList<HashTagRankingBean>();
        final HashTagRankingBean tagRank = new HashTagRankingBean();
        final HashTagRankingBean tagRankBef = new HashTagRankingBean();
        final HashTagRankingBean tagRankAfter = new HashTagRankingBean();
        final Integer hashTagRankListSize = hashTagRankingList.size() - value;
        Integer positionBefore;
        Integer positionAfter;
        log.debug("Hashtag ranking list --->" + hashTagRankListSize);

        for (int i = 0; i < hashTagRankingList.size(); i++) {
            if (hashTagRankingList.get(i).getHashTag().getHashTag()
                    .equals(tagName)) {
                // Retrieve hashtag main.
                position = i;
                tagRank.setAverage(hashTagRankingList.get(i).getAverage());
                tagRank.setPosition(i);
                tagRank.setTagName(tagName);
                tagRank.setRankId(hashTagRankingList.get(i).getRankId());
                tagRankingBeanList.add(tagRank);
                log.debug("HashTag ranking main ---> "
                        + hashTagRankingList.get(i).getHashTag().getHashTag());
                log.debug("HashTag ranking main position---> " + position);
                positionBefore = position - value;
                positionAfter = position + value;
                if ((position > 0) && (position < hashTagRankListSize)) {
                    log.debug(" --- HashTag ranking first option ---");
                    // Save hashTag before item
                    tagRankBef.setAverage(hashTagRankingList
                            .get(positionBefore).getAverage());
                    tagRankBef.setPosition(positionBefore);
                    tagRankBef.setTagName(hashTagRankingList
                            .get(positionBefore).getHashTag().getHashTag());
                    tagRankBef.setRankId(hashTagRankingList.get(positionBefore)
                            .getRankId());
                    tagRankingBeanList.add(tagRankBef);

                    // Save hashTag after item
                    tagRankAfter.setAverage(hashTagRankingList.get(
                            positionAfter).getAverage());
                    tagRankAfter.setPosition(positionAfter);
                    tagRankAfter.setTagName(hashTagRankingList
                            .get(positionAfter).getHashTag().getHashTag());
                    tagRankAfter.setRankId(hashTagRankingList
                            .get(positionAfter).getRankId());
                    tagRankingBeanList.add(tagRankAfter);
                } else if ((position > 0) && (position == hashTagRankListSize)) {
                    log.debug(" --- HashTag ranking second option --- ");
                    // Save hashTag before item
                    tagRankBef.setAverage(hashTagRankingList
                            .get(positionBefore).getAverage());
                    tagRankBef.setPosition(positionBefore);
                    tagRankBef.setTagName(hashTagRankingList
                            .get(positionBefore).getHashTag().getHashTag());
                    tagRankBef.setRankId(hashTagRankingList.get(positionBefore)
                            .getRankId());
                    tagRankingBeanList.add(tagRankBef);
                } else if ((position == 0)) {
                    log.debug(" --- HashTag ranking second option --- ");
                    // Save hashTag after item
                    tagRankAfter.setAverage(hashTagRankingList.get(
                            positionAfter).getAverage());
                    tagRankAfter.setPosition(positionAfter);
                    tagRankAfter.setTagName(hashTagRankingList
                            .get(positionAfter).getHashTag().getHashTag());
                    tagRankAfter.setRankId(hashTagRankingList
                            .get(positionAfter).getRankId());
                    tagRankingBeanList.add(tagRankAfter);
                }
            }
        }
        Collections.sort(tagRankingBeanList);
        return tagRankingBeanList;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#retrieveGenericStats
     * (java.lang.String, org.encuestame.utils.enums.TypeSearchResult)
     */
    public GenericStatsBean retrieveGenericStats(final String itemId,
            final TypeSearchResult itemType) throws EnMeNoResultsFoundException {
        Long totalHits = 0L;
        String createdBy = " ";
        Date createdAt = null;
        double average = 0;
        Long likeDislikeRate = 0L;
        Long likeVotes;
        Long dislikeVotes;
        Long id;
        HashMap<Integer, RelativeTimeEnum> relative;
        if (itemType.equals(TypeSearchResult.TWEETPOLL)) {
            id = new Long(Long.parseLong(itemId));
            final TweetPoll tweetPoll = this.getTweetPoll(id);
            totalHits = tweetPoll.getHits() == null ? 0 : tweetPoll.getHits();
            createdBy = tweetPoll.getEditorOwner().getUsername() == null ? ""
                    : tweetPoll.getEditorOwner().getUsername();
            createdAt = tweetPoll.getCreateDate();
            relative = DateUtil.getRelativeTime(createdAt);
            likeVotes = tweetPoll.getLikeVote() == null ? 0L : tweetPoll
                    .getLikeVote();
            dislikeVotes = tweetPoll.getDislikeVote() == null ? 0L : tweetPoll
                    .getDislikeVote();
            // Like/Dislike Rate = Total Like votes minus total dislike votes.
            likeDislikeRate = (likeVotes - dislikeVotes);

        } else if (itemType.equals(TypeSearchResult.POLL)) {
            id = new Long(Long.parseLong(itemId));
            final Poll poll = this.getPoll(id);
            totalHits = poll.getHits() == null ? 0 : poll.getHits();
            createdBy = poll.getEditorOwner().getUsername();
            createdAt = poll.getCreatedAt();
            relative = DateUtil.getRelativeTime(createdAt);
            likeVotes = poll.getLikeVote() == null ? 0L : poll.getLikeVote();
            dislikeVotes = poll.getDislikeVote() == null ? 0L : poll
                    .getDislikeVote();

        } else if (itemType.equals(TypeSearchResult.SURVEY)) {
            id = new Long(Long.parseLong(itemId));
            final Survey survey = this.getSurvey(id);
            totalHits = survey.getHits();
            createdBy = survey.getEditorOwner().getUsername() == null ? " "
                    : survey.getEditorOwner().getUsername();
            createdAt = survey.getCreatedAt();
            relative = DateUtil.getRelativeTime(createdAt);
            likeVotes = survey.getLikeVote();
            dislikeVotes = survey.getDislikeVote();

        } else if (itemType.equals(TypeSearchResult.HASHTAG)) {
            final HashTag tag = getHashTagItem(itemId);
            totalHits = tag.getHits();
            createdAt = tag.getUpdatedDate();
            relative = DateUtil.getRelativeTime(createdAt);
        }
        final GenericStatsBean genericBean = new GenericStatsBean();
        genericBean.setLikeDislikeRate(likeDislikeRate);
        ;
        genericBean.setHits(totalHits);
        genericBean.setCreatedBy(createdBy);
        genericBean.setAverage(average);
        genericBean.setCreatedAt(createdAt);
        return genericBean;
    }

    public void retrieveHashTagGraphData() {

    }

    /**
     * Get survey by id.
     *
     * @param id
     * @return
     * @throws EnMeNoResultsFoundException
     */
    private Survey getSurvey(final Long id) throws EnMeNoResultsFoundException {
        return getSurveyService().getSurveyById(id);
    }

    /**
     * Get Poll by id.
     *
     * @param id
     * @return
     * @throws EnMeNoResultsFoundException
     */
    private Poll getPoll(final Long id) throws EnMeNoResultsFoundException {
        return getPollService().getPollById(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#getTopRatedProfile(java
     * .lang.Boolean)
     */
    public List<ProfileRatedTopBean> getTopRatedProfile(final Boolean status)
            throws EnMeNoResultsFoundException {
        Long topValue = 0L;
        Long totalTweetPollPublished;
        Long totalPollPublished;
        Long total;

        final List<UserAccount> users = getSecurityService()
                .getUserAccountsAvailable(status);
        final List<ProfileRatedTopBean> profiles = ConvertDomainBean
                .convertUserAccountListToProfileRated(users);
        for (ProfileRatedTopBean profileRatedTopBean : profiles) {
            totalTweetPollPublished = getTweetPollDao().getTotalTweetPoll(
                    getUserAccount(profileRatedTopBean.getUsername()), status);
            log.debug("total tweetPolss published by -->"
                    + totalTweetPollPublished);
            totalPollPublished = getPollDao().getTotalPollsbyUser(
                    getUserAccount(profileRatedTopBean.getUsername()), status);
            log.debug("total tweetPolss published by -->"
                    + totalTweetPollPublished);
            total = totalTweetPollPublished + totalPollPublished;
            topValue = topValue + total;
            log.debug("total value asigned to -->" + totalTweetPollPublished);
            profileRatedTopBean.setTopValue(topValue);
        }
        Collections.sort(profiles);
        return profiles;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.encuestame.core.service.imp.IFrontEndService#getHashTagLinks(org.
     * encuestame.persistence.domain.HashTag)
     */
    public List<LinksSocialBean> getHashTagLinks(final HashTag hash) {
        final List<TweetPollSavedPublishedStatus> links = getFrontEndDao()
                .getLinksByHomeItem(hash, null, null, null, null,
                        TypeSearchResult.HASHTAG);
        log.debug("getTweetPollLinks " + links.size());
        return ConvertDomainBean.convertTweetPollSavedPublishedStatus(links);
    }

    // Total Usage by item.
    public void getTotalUsagebyHashTagAndDateRange() {

    }

    /**
     * Create hashTag details stats.
     *
     * @param label
     * @param value
     * @return
     */
    private HashTagDetailStats createTagDetailsStats(final String label,
            final Long value) {
        final HashTagDetailStats tagDetails = new HashTagDetailStats();
        tagDetails.setLabel(label);
        tagDetails.setValue(value);
        return tagDetails;
    }

    /**
     * Counter items by HashTag.
     *
     * @param month
     * @param counter
     * @return
     */
    private Long counterItemsbyHashTag(final int month, Long counter) {
        switch (month) {
        case 1:
            counter++;
            break;

        case 2:
            counter++;
            break;

        case 3:
            counter++;
            break;

        case 4:
            counter++;
            break;

        case 5:
            counter++;
            break;

        case 6:
            counter++;
            break;

        case 7:
            counter++;
            break;

        case 8:
            counter++;
            break;

        case 9:
            counter++;
            break;

        case 10:
            counter++;
            break;

        case 11:
            counter++;
            break;

        case 12:
            counter++;
            break;

        default:
            log.debug("Month not found");
        }

        return counter;
    }

    /**
     * Get item creation date value
     *
     * @param tpolls
     * @param polls
     * @param surveys
     * @param counter
     * @return
     */
    private DateTime getItemCreationDate(final List<TweetPoll> tpolls,
            final List<Poll> polls, final List<Survey> surveys,
            final int counter) {
        DateTime monthValue;
        if (tpolls != null) {
            monthValue = new DateTime(tpolls.get(counter).getCreateDate());

        } else if (polls != null) {
            monthValue = new DateTime(polls.get(counter).getCreatedAt());

        } else {
            monthValue = new DateTime(surveys.get(counter).getCreatedAt());
        }
        return monthValue;
    }

    /**
     *
     * @param totalList
     * @param tpolls
     * @param polls
     * @param surveys
     * @param iValue
     * @return
     */
    private List<HashTagDetailStats> addHashTagDetailedStatsbyItem(
            final int totalList, final List<TweetPoll> tpolls,
            final List<Poll> polls, final List<Survey> surveys, final int iValue) {
        int month = 0;

        int afterMonthValue = 0;
        int afterMonthIndexValue = 0;

        DateTime currentMonthDate = this.getItemCreationDate(tpolls, polls,
                surveys, iValue);
        month = currentMonthDate.getMonthOfYear();

        if (iValue < totalList - 1) {
            afterMonthIndexValue = iValue + 1;
            DateTime dt2 = this.getItemCreationDate(tpolls, polls, surveys,
                    afterMonthIndexValue);
            afterMonthValue = dt2.getMonthOfYear();

        } else {
            afterMonthIndexValue = iValue;
            afterMonthValue = 0;
        }
        counterItemsbyMonth = this.counterItemsbyHashTag(month,
                counterItemsbyMonth);
        if (month != afterMonthValue) {
            hashTagItemDetailedStats = this.createTagDetailsStats(
                    String.valueOf(month), counterItemsbyMonth);
            tagStatsDetail.add(hashTagItemDetailedStats);

            counterItemsbyMonth = 0L;
        }
        return tagStatsDetail;
    }


    private List<HashTagDetailStats> getHashTagItemUsageDetailedByDateRange(final int totalList, final List<TweetPoll> tpolls, final List<Survey> surveys, final List<Poll> polls ){
        List<HashTagDetailStats> statDetail = new ArrayList<HashTagDetailStats>();
        if (totalList > 0) {
            log.debug(" Total items by hashTag  ---> " + totalList);
            for (int i = 0; i < totalList; i++) {
                statDetail = this.addHashTagDetailedStatsbyItem(totalList,
                        tpolls, polls, surveys, i);
            }
        } else
            log.error("Items by HashTag not found");
        return statDetail;
    }


    /**
     *
     * @param tpolls
     * @param surveys
     * @param polls
     * @return
     */
    private List<HashTagDetailStats> getTotalPoll(final List<TweetPoll> tpolls,
            final List<Survey> surveys, final List<Poll> polls) {
        int totalList = 0;
        List<HashTagDetailStats> itemStatDetail = new ArrayList<HashTagDetailStats>();
        if (tpolls.size() > 0) {
            totalList = tpolls.size();
            itemStatDetail = this.getHashTagItemUsageDetailedByDateRange(totalList, tpolls, null, null);
        }
        if (polls.size() > 0) {
            totalList = polls.size();
            itemStatDetail = this.getHashTagItemUsageDetailedByDateRange(totalList, null, null, polls);

        }
        if (surveys.size() > 0) {
            totalList = surveys.size();
            itemStatDetail = this.getHashTagItemUsageDetailedByDateRange(totalList, null, surveys, null);
        } else {
            log.error("Items by HashTag not found");
        }

        return itemStatDetail;
    }


    /**
     * Get total tweetpoll usage stats by hastag and date range.
     * @param tagName
     * @param period
     * @param startResults
     * @param maxResults
     * @return
     */
    private List<TweetPoll> getTotalTweetPollUsageByHashTagAndDateRange(
            final String tagName, final Integer period,
            final Integer startResults, final Integer maxResults) {
        List<TweetPoll> tweetPollsByHashTag = new ArrayList<TweetPoll>();
        // Gets the tweetpolls by hashtag
        tweetPollsByHashTag = getTweetPollDao()
                .getTweetPollsbyHashTagNameAndDateRange(tagName, period,
                        startResults, maxResults);
        // Gets the stats detail of hashtags by tweetpoll.
        return tweetPollsByHashTag;
    }

    /**
     * Get total poll usage stats by hastag and date range.
     * @param tagName
     * @param period
     * @param startResults
     * @param maxResults
     * @return
     */
    private List<Poll> getTotalPollUsageByHashTagAndDateRange(
            final String tagName, final Integer period,
            final Integer startResults, final Integer maxResults) {
        List<Poll> pollsByHashTag = new ArrayList<Poll>();
        pollsByHashTag = getPollDao().getPollsbyHashTagNameAndDateRange(
                tagName, period, startResults, maxResults);
        return pollsByHashTag;
    }

    /**
     * Get total survey usage by HashTag name and date range.
     * @param tagName
     * @param period
     * @param startResults
     * @param maxResults
     * @return
     */
    private List<Survey> getTotalSurveyUsageByHashTagAndDateRange(
            final String tagName, final Integer period,
            final Integer startResults, final Integer maxResults) {
        List<Survey> surveysByHashTag = new ArrayList<Survey>();
        surveysByHashTag = getSurveyDaoImp()
                .getSurveysbyHashTagNameAndDateRange(tagName, period,
                        startResults, maxResults);
        return surveysByHashTag;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.encuestame.core.service.imp.IFrontEndService#
     * getTotalUsagebyHashTagAndDateRange(java.lang.String, java.lang.Integer,
     * java.lang.Integer, java.lang.Integer)
     */
    public List<HashTagDetailStats> getTotalUsagebyHashTagAndDateRange(
            final String hashTagName, final Integer period,
            final Integer startResults, final Integer maxResults)
            throws EnMeNoResultsFoundException {
        // Check if the hashtag exists
        final HashTag tag = this.getHashTag(hashTagName, Boolean.TRUE);

        List<HashTagDetailStats> hashTagDetailedStatisticsList = new ArrayList<HashTagDetailStats>();

        List<HashTagDetailStats> hashTagUsagebyItemAndDateRange = new ArrayList<HashTagDetailStats>();

        List<TweetPoll> tweetPollsByDateRange = new ArrayList<TweetPoll>();
        List<Poll> pollsByDateRange = new ArrayList<Poll>();
        List<Survey> surveysByDateRange = new ArrayList<Survey>();
        // If the tag exists then obtains the total

        if (tag != null) {
            tweetPollsByDateRange = this
                    .getTotalTweetPollUsageByHashTagAndDateRange(hashTagName,
                            period, startResults, maxResults);

            pollsByDateRange = this.getTotalPollUsageByHashTagAndDateRange(
                    hashTagName, period, startResults, maxResults);

            surveysByDateRange = this.getTotalSurveyUsageByHashTagAndDateRange(
                    hashTagName, period, startResults, maxResults);
            hashTagUsagebyItemAndDateRange = this
                    .getTotalPoll(tweetPollsByDateRange, surveysByDateRange,
                            pollsByDateRange);

        }
        hashTagDetailedStatisticsList = this.getHashTagStatsDetailedtList(hashTagUsagebyItemAndDateRange);
        return hashTagDetailedStatisticsList;
    }

    /**
     * Get hashtag stats detailed list orderly.
     * @param totalHashTagStatsbyItem
     * @return
     */
    private List<HashTagDetailStats> getHashTagStatsDetailedtList(
            final List<HashTagDetailStats> totalHashTagStatsbyItem) {

        List<HashTagDetailStats> hashTagStatDetailedList = new ArrayList<HashTagDetailStats>();
        String previousItemValue = "0";
        Long actualLabelValue;
        Long previousLabelValue;
        Long newItemValue;
        String hashTagStatActualLabel;
        Collections.sort(totalHashTagStatsbyItem);
        for (int i = 0; i < totalHashTagStatsbyItem.size(); i++) {

            if (totalHashTagStatsbyItem.get(i).getLabel().equals(previousItemValue)) {
                actualLabelValue = totalHashTagStatsbyItem.get(i).getValue();
                hashTagStatActualLabel = totalHashTagStatsbyItem.get(i).getLabel();

                for (int j = 0; j < hashTagStatDetailedList.size(); j++) {
                    if (hashTagStatDetailedList.get(j).getLabel()
                            .equals(hashTagStatActualLabel)) {
                        previousLabelValue = hashTagStatDetailedList.get(j)
                                .getValue();
                        newItemValue = actualLabelValue + previousLabelValue;
                        hashTagStatDetailedList.get(j).setValue(newItemValue);
                    }
                }
            } else {

                hashTagStatDetailedList.add(totalHashTagStatsbyItem.get(i));
            }

            previousItemValue = totalHashTagStatsbyItem.get(i).getLabel();
        }

        return hashTagStatDetailedList;
    }

    /**
     * @return the tweetPollService
     */
    public TweetPollService getTweetPollService() {
        return tweetPollService;
    }

    /**
     * @param tweetPollService
     *            the tweetPollService to set
     */
    public void setTweetPollService(TweetPollService tweetPollService) {
        this.tweetPollService = tweetPollService;
    }

    /**
     * @return the pollService
     */
    public PollService getPollService() {
        return pollService;
    }

    /**
     * @param pollService
     *            the pollService to set
     */
    public void setPollService(final PollService pollService) {
        this.pollService = pollService;
    }

    /**
     * @return the surveyService
     */
    public SurveyService getSurveyService() {
        return surveyService;
    }

    /**
     * @param surveyService
     *            the surveyService to set
     */
    public void setSurveyService(final SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    /**
     * @return the securityService
     */
    public SecurityOperations getSecurityService() {
        return securityService;
    }

    /**
     * @param securityService
     *            the securityService to set
     */
    public void setSecurityService(SecurityOperations securityService) {
        this.securityService = securityService;
    }

}