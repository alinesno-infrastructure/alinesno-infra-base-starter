package com.alinesno.infra.base.starter.api.controller;

import com.alinesno.infra.base.starter.entity.ProjectMenusEntity;
import com.alinesno.infra.base.starter.service.IProjectMenusService;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建示例应用菜单 前端控制器
 *
 * @version 1.0.0
 * @since 2023/9/30 17:11:56
 */
@Slf4j
@Api(tags = "创建示例应用菜单")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/base/starter/projectMenus")
public class ProjectMenusController extends BaseController<ProjectMenusEntity, IProjectMenusService> {

    @Autowired
    private IProjectMenusService projectMenusService;

    /**
     * 获取创建示例应用菜单的DataTables数据
     *
     * @param request HttpServletRequest对象
     * @param model Model对象
     * @param page DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, getFeign(), page);
    }

    @Override
    public IProjectMenusService getFeign() {
        return projectMenusService;
    }
}
