//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.infra.dataobject.BoardDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import io.choerodon.agile.api.vo.BoardVO;
//import io.choerodon.agile.domain.agile.entity.BoardE;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/5/14.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class BoardConverter implements ConvertorI<BoardE, BoardDTO, BoardVO> {
//
//    @Override
//    public BoardE dtoToEntity(BoardVO boardVO) {
//        BoardE boardE = new BoardE();
//        BeanUtils.copyProperties(boardVO, boardE);
//        return boardE;
//    }
//
//    @Override
//    public BoardVO entityToDto(BoardE boardE) {
//        BoardVO boardVO = new BoardVO();
//        BeanUtils.copyProperties(boardE, boardVO);
//        return boardVO;
//    }
//
//    @Override
//    public BoardE doToEntity(BoardDTO boardDTO) {
//        BoardE boardE = new BoardE();
//        BeanUtils.copyProperties(boardDTO, boardE);
//        return boardE;
//    }
//
//    @Override
//    public BoardDTO entityToDo(BoardE boardE) {
//        BoardDTO boardDTO = new BoardDTO();
//        BeanUtils.copyProperties(boardE, boardDTO);
//        return boardDTO;
//    }
//
//    @Override
//    public BoardVO doToDto(BoardDTO boardDTO) {
//        BoardVO boardVO = new BoardVO();
//        BeanUtils.copyProperties(boardDTO, boardVO);
//        return boardVO;
//    }
//
//    @Override
//    public BoardDTO dtoToDo(BoardVO boardVO) {
//        BoardDTO boardDTO = new BoardDTO();
//        BeanUtils.copyProperties(boardVO, boardDTO);
//        return boardDTO;
//    }
//}
