<template>
  <div class="app-container">
    <el-row :gutter="20">
       <!--应用数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

          <el-form-item label="应用名称" prop="name">
            <el-input v-model="queryParams['condition[name|like]']" placeholder="请输入应用名称" clearable style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>
          <el-form-item label="显示名称" prop="showName" label-width="100px">
            <el-input v-model="queryParams['condition[showName|like]']" placeholder="请输入显示名称" clearable style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <!-- <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
          </el-col> -->
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="ApplicationList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column label="图标" align="center" width="60px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
                <div class="role-icon">
                  <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + ((scope.$index + 1)%10 + 5) + '.png'" />
                </div>
            </template>
          </el-table-column>
          <el-table-column label="应用名称" align="left" key="name" prop="name" v-if="columns[1].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <span style="cursor: pointer;" @click="()=>detailInfo()">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="显示名称" align="left" key="showName" prop="showName" v-if="columns[2].visible" :show-overflow-tooltip="true"/>
          <el-table-column label="领域" align="left" key="domain" prop="domain" v-if="columns[3].visible" :show-overflow-tooltip="true"/>
          
          <el-table-column label="集成插件" align="center" width="120"  key="domainName" prop="domainName" v-if="columns[4].visible" :show-overflow-tooltip="true">
            <template #default="scope">
                <el-button type="primary" text bg icon="Paperclip" @click="configPrompt(scope.row)">配置</el-button>
            </template>
          </el-table-column>
          <el-table-column label="集成仓库" align="center" width="120"  key="storagePath" prop="storagePath" v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
                <el-button type="primary" text bg icon="Paperclip" @click="configPrompt(scope.row)">配置</el-button>
            </template>
          </el-table-column>
          <el-table-column label="是否发布" align="center" width="120" key="target" prop="target" v-if="columns[6].visible" :show-overflow-tooltip="true">
            <template #default="scope">
                <el-button type="primary" text bg icon="Paperclip" @click="configPrompt(scope.row)">配置</el-button>
            </template>
          </el-table-column>

          <!--
          <el-table-column label="创建时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
          </el-table-column>
          -->

          <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width" v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
      </el-col>
    </el-row>

    <!-- 添加或修改应用配置对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form :model="form" :rules="rules" ref="ApplicationRef" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item  label="应用名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入应用名称" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="显示名称" prop="showName">
              <el-input v-model="form.showName" placeholder="请输入显示名称" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="域名" prop="domainName">
              <el-input v-model="form.domainName" placeholder="请输入域名" maxlength="100"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="所属领域" prop="domain">
              <el-input v-model="form.domain" placeholder="请输入所属领域" maxlength="100"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="安全存储路径" prop="storagePath" label-width="107px">
              <el-input v-model="form.storagePath" placeholder="请输入安全存储路径" maxlength="200"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="应用目标" prop="target">
              <el-input v-model="form.target" placeholder="请输入应用目标" maxlength="20"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Application">

import {
  listApplication,
  delApplication,
  getApplication,
  updateApplication,
  addApplication,
} from "@/api/base/starter/application";
import {reactive} from "vue";


const router = useRouter();
const {proxy} = getCurrentInstance();
const ApplicationList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

// 列显隐信息
const columns = ref([
  {key: 0, label: `图标`, visible: true},
  {key: 1, label: `应用名称`, visible: true},
  {key: 2, label: `显示名称`, visible: true},
  {key: 3, label: `所属领域`, visible: true},
  {key: 4, label: `域名`, visible: true},
  {key: 5, label: `安全存储路径`, visible: true},
  {key: 6, label: `应用目标`, visible: true},
  {key: 7, label: `创建时间`, visible: true},
  {key: 8, label: `编辑`, visible: true},

]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    name: undefined,
    showName: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    id: [{required: true, message: "应用编号不能为空", trigger: "blur"}],
    name: [{required: true, message: "应用名称不能为空", trigger: "blur"}, {min: 2, max: 20, message: "应用名称长度必须介于 2 和 20 之间", trigger: "blur"}],
    showName: [{required: true, message: "显示名称不能为空", trigger: "blur"}],
    domain: [{required: true, message: "所属领域不能为空", trigger: "blur"}],
    domainName: [{required: true, message: "域名不能为空", trigger: "blur"}],
    storagePath: [{required: true, message: "安全存储路径不能为空", trigger: "blur"}],
    target: [{required: true, message: "应用目标不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listApplication(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    ApplicationList.value = res.rows;
    total.value = res.total;
  });
};

/** 搜索按钮操作 */
function handleQuery() {
  console.log(queryParams);
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  queryParams.value.name = undefined;
  queryParams.value.showName = undefined;
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const id = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除应用编号为"' + id + '"的数据项？').then(function () {
    return delApplication(id);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
};

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 重置操作表单 */
function reset() {
  form.value = {
    id: undefined,
    name: undefined,
    showName: undefined,
    domain: undefined,
    domainName: undefined,
    storagePath: undefined,
    target: undefined,
  };
  proxy.resetForm("ApplicationRef");
};

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
};

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加应用";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getApplication(id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改应用";

  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ApplicationRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateApplication(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addApplication(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

/** 项目详情 */
function detailInfo(){
  router.push({ path: "/function/base/starter/application/detail" });
}

getList();
</script>
