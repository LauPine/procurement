package cn.com.liucm.controller;

import cn.com.liucm.dto.PageDto;
import cn.com.liucm.dto.PurchaseInfoDto;
import cn.com.liucm.dto.ResultDto;
import cn.com.liucm.service.PurchaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("purchaseInfo/*")
public class PurchaseInfoController extends BaseController{
    @Autowired
    PurchaseInfoService purchaseInfoService;

    /**
     * 导入采购信息
     */
    @ResponseBody
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public ResultDto upload(@RequestParam("file") MultipartFile file) {
        purchaseInfoService.upload(file);
        return ajaxDoneSuccess();
    }

    /**
     * 查询采购信息
     */
    @ResponseBody
    @RequestMapping(value = "queryPurchaseInfo", method = RequestMethod.POST)
    public PageDto<PurchaseInfoDto> queryPurchaseInfo(PurchaseInfoDto purchaseInfoDto) {
        return purchaseInfoService.queryPurchaseInfo(purchaseInfoDto);
    }

    /**
     * 根据结果查询采购信息
     */
    @ResponseBody
    @RequestMapping(value = "queryPurchaseInfoByResult", method = RequestMethod.POST)
    public List<PurchaseInfoDto> queryPurchaseInfoByResult(PurchaseInfoDto purchaseInfoDto) {
        return purchaseInfoService.queryPurchaseInfoByResult(purchaseInfoDto);
    }

    /**
     * 更新采购信息
     */
    @ResponseBody
    @RequestMapping(value = "updatePurchaseInfo", method = RequestMethod.POST)
    public ResultDto updatePurchaseInfo(@RequestBody List<PurchaseInfoDto> purchaseInfoDtoList) {
        purchaseInfoService.updatePurchaseInfo(purchaseInfoDtoList);
        return ajaxDoneSuccess();
    }

    /**
     * 获取无查询结果的采购信息
     */
    @ResponseBody
    @RequestMapping(value = "getNoResultAS", method = RequestMethod.POST)
    public ResultDto getNoResultAS(PurchaseInfoDto purchaseInfoDto) {
        return ajaxDoneSuccess(purchaseInfoService.getNoResultAS(purchaseInfoDto));
    }

    /**
     * 根据结果导出采购信息
     */
    @ResponseBody
    @RequestMapping(value = "exportPurchaseInfo", method = RequestMethod.GET)
    public ModelAndView exportPurchaseInfo(HttpServletResponse response, PurchaseInfoDto purchaseInfoDto){
        try {
            purchaseInfoService.exportPurchaseInfo(purchaseInfoDto, response);
        }catch (Exception e){
            return new ModelAndView("exportError");
        }
        return null;
    }

    /**
     * 导出采购信息
     */
    @ResponseBody
    @RequestMapping(value = "exportPurchaseInfoByResult", method = RequestMethod.GET)
    public ModelAndView exportPurchaseInfoByResult(HttpServletResponse response, PurchaseInfoDto purchaseInfoDto){
        try {
            purchaseInfoService.exportPurchaseInfoByResult(purchaseInfoDto, response);
        }catch (Exception e){
            return new ModelAndView("exportError");
        }
        return null;
    }

    /**
     * 删除采购信息
     */
    @ResponseBody
    @RequestMapping(value = "deletePurchaseInfo", method = RequestMethod.POST)
    public ResultDto deletePurchaseInfo(@RequestBody List<String> companyNoList) {
        purchaseInfoService.deletePurchaseInfo(companyNoList);
        return ajaxDoneSuccess();
    }
}
