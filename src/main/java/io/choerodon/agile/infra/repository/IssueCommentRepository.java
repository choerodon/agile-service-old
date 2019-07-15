//package io.choerodon.agile.infra.repository;
//
//import io.choerodon.agile.domain.agile.entity.IssueCommentE;
//import io.choerodon.agile.infra.dataobject.IssueCommentDTO;
//
//
///**
// * 敏捷开发Issue评论
// *
// * @author dinghuang123@gmail.com
// * @since 2018-05-14 21:59:45
// */
//public interface IssueCommentRepository {
//
//    /**
//     * 更新敏捷开发Issue评论
//     *
//     * @param issueCommentE issueCommentE
//     * @param fieldList     fieldList
//     * @return IssueCommentE
//     */
//    IssueCommentE update(IssueCommentE issueCommentE, String[] fieldList);
//
//    /**
//     * 添加一个敏捷开发Issue评论
//     *
//     * @param issueCommentE issueCommentE
//     * @return IssueCommentE
//     */
//    IssueCommentE create(IssueCommentE issueCommentE);
//
//    /**
//     * 根据参数删除敏捷开发Issue评论
//     *
//     * @param issueCommentDTO issueCommentDTO
//     * @return int
//     */
//    int delete(IssueCommentDTO issueCommentDTO);
//}