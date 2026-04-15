SET NAMES utf8mb4;

-- 大富翁金融服务公司 贷款业务定制化增量脚本
-- 适配 RuoYi-Cloud（system 模块）

-- ----------------------------
-- 1、贷款客户表
-- ----------------------------
create table if not exists loan_customer (
  customer_id          bigint(20)      not null auto_increment comment '客户ID',
  customer_name        varchar(64)     not null comment '客户姓名',
  customer_type        char(1)         default 'P' comment '客户类型（P个人 C企业）',
  phone                varchar(20)     not null comment '联系电话',
  id_card_no           varchar(30)     default null comment '身份证号',
  company_name         varchar(100)    default null comment '公司名称',
  business_license_no  varchar(50)     default null comment '营业执照号',
  source_type          varchar(20)     default 'PHONE' comment '客户来源',
  intent_level         varchar(10)     default 'C' comment '意向等级',
  intent_amount        decimal(14,2)   default 0.00 comment '意向金额',
  status               varchar(20)     default 'PRIVATE' comment '客户状态（PRIVATE/SEA/SIGNED）',
  user_id              bigint(20)      default null comment '归属销售',
  dept_id              bigint(20)      default null comment '归属部门',
  last_follow_time     datetime        default null comment '最近跟进时间',
  next_follow_time     datetime        default null comment '下次跟进时间',
  signed_time          datetime        default null comment '签约时间',
  sea_time             datetime        default null comment '进入公海时间',
  transfer_remark      varchar(500)    default null comment '转移说明',
  create_by            varchar(64)     default '' comment '创建者',
  create_time          datetime        default null comment '创建时间',
  update_by            varchar(64)     default '' comment '更新者',
  update_time          datetime        default null comment '更新时间',
  remark               varchar(500)    default null comment '备注',
  primary key (customer_id),
  key idx_loan_customer_phone (phone),
  key idx_loan_customer_id_card_no (id_card_no),
  key idx_loan_customer_business_license_no (business_license_no),
  key idx_loan_customer_status (status),
  key idx_loan_customer_user_id (user_id),
  key idx_loan_customer_dept_id (dept_id)
) engine=innodb comment='贷款客户表';

-- ----------------------------
-- 2、客户跟进记录表
-- ----------------------------
create table if not exists loan_customer_follow (
  follow_id            bigint(20)      not null auto_increment comment '跟进ID',
  customer_id          bigint(20)      not null comment '客户ID',
  follow_type          varchar(20)     default 'PHONE' comment '跟进方式',
  follow_content       varchar(2000)   not null comment '跟进内容',
  valid_flag           char(1)         default '1' comment '是否有效（1是 0否）',
  intent_level         varchar(10)     default null comment '跟进后意向等级',
  next_follow_time     datetime        default null comment '下次跟进时间',
  user_id              bigint(20)      default null comment '跟进人ID',
  user_name            varchar(64)     default null comment '跟进人',
  create_by            varchar(64)     default '' comment '创建者',
  create_time          datetime        default null comment '创建时间',
  update_by            varchar(64)     default '' comment '更新者',
  update_time          datetime        default null comment '更新时间',
  remark               varchar(500)    default null comment '备注',
  primary key (follow_id),
  key idx_loan_follow_customer_id (customer_id),
  key idx_loan_follow_create_time (create_time)
) engine=innodb comment='客户跟进记录表';

-- ----------------------------
-- 3、贷款合同表
-- ----------------------------
create table if not exists loan_contract (
  contract_id          bigint(20)      not null auto_increment comment '合同ID',
  contract_no          varchar(64)     not null comment '合同编号',
  customer_id          bigint(20)      not null comment '客户ID',
  user_id              bigint(20)      default null comment '销售ID',
  dept_id              bigint(20)      default null comment '部门ID',
  product_name         varchar(100)    default null comment '金融产品',
  attachment_urls      text            comment '附件地址',
  contract_amount      decimal(14,2)   default 0.00 comment '合同金额',
  disburse_amount      decimal(14,2)   default 0.00 comment '发放金额',
  first_fee_amount     decimal(14,2)   default 0.00 comment '前置服务费应收',
  second_fee_amount    decimal(14,2)   default 0.00 comment '放款后服务费应收',
  first_fee_paid       decimal(14,2)   default 0.00 comment '前置服务费实收',
  second_fee_paid      decimal(14,2)   default 0.00 comment '放款后服务费实收',
  status               varchar(20)     default 'SIGNED' comment '合同状态',
  sign_time            datetime        default null comment '签约时间',
  submit_time          datetime        default null comment '提交审核时间',
  finance_audit_time   datetime        default null comment '金融审核时间',
  bank_loan_time       datetime        default null comment '银行放款时间',
  finance_user_id      bigint(20)      default null comment '金融审核员ID',
  bank_result          varchar(500)    default null comment '银行反馈',
  create_by            varchar(64)     default '' comment '创建者',
  create_time          datetime        default null comment '创建时间',
  update_by            varchar(64)     default '' comment '更新者',
  update_time          datetime        default null comment '更新时间',
  remark               varchar(500)    default null comment '备注',
  primary key (contract_id),
  unique key uk_loan_contract_no (contract_no),
  key idx_loan_contract_customer_id (customer_id),
  key idx_loan_contract_user_id (user_id),
  key idx_loan_contract_dept_id (dept_id),
  key idx_loan_contract_status (status)
) engine=innodb comment='贷款合同表';

-- ----------------------------
-- 4、服务费收款记录表
-- ----------------------------
create table if not exists loan_fee_record (
  fee_id               bigint(20)      not null auto_increment comment '记录ID',
  contract_id          bigint(20)      not null comment '合同ID',
  stage                varchar(10)     not null comment '收费阶段（PRE/POST）',
  amount               decimal(14,2)   not null default 0.00 comment '收款金额',
  pay_method           varchar(20)     default 'TRANSFER' comment '支付方式',
  payer_name           varchar(64)     default null comment '付款人',
  pay_time             datetime        default null comment '收款时间',
  collector_user_id    bigint(20)      default null comment '收款人ID',
  collector_user_name  varchar(64)     default null comment '收款人',
  status               varchar(20)     default 'PAID' comment '状态（PAID/REFUND）',
  create_by            varchar(64)     default '' comment '创建者',
  create_time          datetime        default null comment '创建时间',
  update_by            varchar(64)     default '' comment '更新者',
  update_time          datetime        default null comment '更新时间',
  remark               varchar(500)    default null comment '备注',
  primary key (fee_id),
  key idx_loan_fee_contract_id (contract_id),
  key idx_loan_fee_stage (stage)
) engine=innodb comment='服务费收款记录表';

-- ----------------------------
-- 5、系统参数（超期释放天数）
-- ----------------------------
create table if not exists loan_work_log (
  log_id                bigint(20)      not null auto_increment comment '日志ID',
  work_date             date            not null comment '工作日期',
  user_id               bigint(20)      not null comment '销售ID',
  dept_id               bigint(20)      default null comment '部门ID',
  call_count            int(11)         default 0 comment '电话数',
  valid_call_count      int(11)         default 0 comment '有效电话数',
  intent_customer_count int(11)         default 0 comment '意向客户数',
  visit_count           int(11)         default 0 comment '面谈客户数',
  signed_count          int(11)         default 0 comment '签约数',
  create_by             varchar(64)     default '' comment '创建者',
  create_time           datetime        default null comment '创建时间',
  update_by             varchar(64)     default '' comment '更新者',
  update_time           datetime        default null comment '更新时间',
  remark                varchar(500)    default null comment '备注',
  primary key (log_id),
  unique key uk_loan_worklog_user_date (user_id, work_date),
  key idx_loan_worklog_work_date (work_date),
  key idx_loan_worklog_dept_id (dept_id)
) engine=innodb comment='销售工作日志';

insert into sys_config(config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
select '贷款客户-公海释放天数', 'loan.customer.releaseDays', '7', 'N', 'admin', sysdate(), '', null, '客户若超过该天数未跟进自动转公海'
from dual
where not exists (
  select 1 from sys_config where config_key = 'loan.customer.releaseDays'
);

-- ----------------------------
-- 6、菜单与权限（贷款业务）
-- ----------------------------
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3000, '贷款业务', 0, 5, 'loan', null, '', '', 1, 0, 'M', '0', '0', '', 'money', 'admin', sysdate(), '', null, '贷款业务目录'
from dual
where not exists (select 1 from sys_menu where menu_id = 3000);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3001, '客户管理', 3000, 1, 'customer', 'loan/customer/index', '', '', 1, 0, 'C', '0', '0', 'loan:customer:list', 'user', 'admin', sysdate(), '', null, '贷款客户管理菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 3001);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3002, '合同管理', 3000, 2, 'contract', 'loan/contract/index', '', '', 1, 0, 'C', '0', '0', 'loan:contract:list', 'date', 'admin', sysdate(), '', null, '贷款合同管理菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 3002);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3003, '工作日志', 3000, 3, 'worklog', 'loan/worklog/index', '', '', 1, 0, 'C', '0', '0', 'loan:worklog:list', 'edit', 'admin', sysdate(), '', null, '销售工作日志菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 3003);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3004, '业绩看板', 3000, 4, 'performance', 'loan/performance/index', '', '', 1, 0, 'C', '0', '0', 'loan:performance:view', 'chart', 'admin', sysdate(), '', null, '贷款业绩可视化看板'
from dual
where not exists (select 1 from sys_menu where menu_id = 3004);

-- 客户管理按钮
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3010, '客户查询', 3001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3010);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3011, '客户新增', 3001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:add', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3011);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3012, '客户修改', 3001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:edit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3012);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3013, '客户删除', 3001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:remove', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3013);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3014, '客户转移', 3001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:transfer', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3014);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3015, '客户转公海', 3001, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:sea', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3015);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3016, '领取公海客户', 3001, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:claim', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3016);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3017, '释放超期客户', 3001, 8, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:release', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3017);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3018, '客户跟进新增', 3001, 9, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:follow:add', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3018);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3019, '客户跟进删除', 3001, 10, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:customer:follow:remove', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3019);

-- 合同管理按钮
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3020, '合同查询', 3002, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:contract:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3020);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3021, '合同新增', 3002, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:contract:add', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3021);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3022, '合同修改', 3002, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:contract:edit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3022);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3023, '合同删除', 3002, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:contract:remove', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3023);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3024, '提交金融审核', 3002, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:contract:submit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3024);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3025, '金融审核', 3002, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:contract:audit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3025);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3026, '服务费收款', 3002, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:contract:fee', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3026);

-- 工作日志按钮
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3030, '日志查询', 3003, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:worklog:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3030);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3031, '日志新增', 3003, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:worklog:add', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3031);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3032, '日志修改', 3003, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:worklog:edit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3032);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3033, '日志删除', 3003, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:worklog:remove', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3033);

-- 业绩分析按钮
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3040, '业绩总览', 3004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:performance:view', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3040);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3041, '部门业绩统计', 3004, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:performance:dept', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3041);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3042, '战区业绩统计', 3004, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:performance:zone', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3042);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3043, '业绩排名', 3004, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:performance:rank', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3043);

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3044, '提成测算', 3004, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'loan:performance:commission', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3044);

-- 普通角色默认授权（如不需要可手动删除）
insert into sys_role_menu(role_id, menu_id)
select 2, 3000 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3000);
insert into sys_role_menu(role_id, menu_id)
select 2, 3001 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3001);
insert into sys_role_menu(role_id, menu_id)
select 2, 3002 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3002);
insert into sys_role_menu(role_id, menu_id)
select 2, 3003 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3003);
insert into sys_role_menu(role_id, menu_id)
select 2, 3004 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3004);
insert into sys_role_menu(role_id, menu_id)
select 2, 3010 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3010);
insert into sys_role_menu(role_id, menu_id)
select 2, 3011 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3011);
insert into sys_role_menu(role_id, menu_id)
select 2, 3012 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3012);
insert into sys_role_menu(role_id, menu_id)
select 2, 3013 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3013);
insert into sys_role_menu(role_id, menu_id)
select 2, 3014 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3014);
insert into sys_role_menu(role_id, menu_id)
select 2, 3015 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3015);
insert into sys_role_menu(role_id, menu_id)
select 2, 3016 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3016);
insert into sys_role_menu(role_id, menu_id)
select 2, 3017 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3017);
insert into sys_role_menu(role_id, menu_id)
select 2, 3018 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3018);
insert into sys_role_menu(role_id, menu_id)
select 2, 3019 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3019);
insert into sys_role_menu(role_id, menu_id)
select 2, 3020 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3020);
insert into sys_role_menu(role_id, menu_id)
select 2, 3021 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3021);
insert into sys_role_menu(role_id, menu_id)
select 2, 3022 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3022);
insert into sys_role_menu(role_id, menu_id)
select 2, 3023 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3023);
insert into sys_role_menu(role_id, menu_id)
select 2, 3024 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3024);
insert into sys_role_menu(role_id, menu_id)
select 2, 3025 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3025);
insert into sys_role_menu(role_id, menu_id)
select 2, 3026 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3026);
insert into sys_role_menu(role_id, menu_id)
select 2, 3030 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3030);
insert into sys_role_menu(role_id, menu_id)
select 2, 3031 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3031);
insert into sys_role_menu(role_id, menu_id)
select 2, 3032 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3032);
insert into sys_role_menu(role_id, menu_id)
select 2, 3033 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3033);
insert into sys_role_menu(role_id, menu_id)
select 2, 3040 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3040);
insert into sys_role_menu(role_id, menu_id)
select 2, 3041 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3041);
insert into sys_role_menu(role_id, menu_id)
select 2, 3042 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3042);
insert into sys_role_menu(role_id, menu_id)
select 2, 3043 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3043);
insert into sys_role_menu(role_id, menu_id)
select 2, 3044 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3044);

-- ----------------------------
-- 7.1、Nacos菜单地址修复（v3控制台端口）
-- ----------------------------
update sys_menu
set path = 'http://localhost:8849/index.html',
    is_frame = 0,
    component = '',
    route_name = '',
    update_by = 'admin',
    update_time = sysdate(),
    remark = 'Nacos控制台（v3控制台端口 8849）'
where menu_id = 112;

-- ----------------------------
-- 7、移除默认“若依官网”菜单（适配本项目）
-- ----------------------------
delete from sys_role_menu where menu_id = 4;
delete from sys_menu where menu_id = 4;

-- ----------------------------
-- 8、组织架构（2个战区 + 8个销售部 + 金融部）
-- ----------------------------
insert into sys_dept(
  dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email,
  status, del_flag, create_by, create_time, update_by, update_time
)
select p.dept_id, p.parent_id, p.ancestors, p.dept_name, p.order_num, p.leader, p.phone, p.email,
       '0', '0', 'admin', sysdate(), '', null
from (
  select 200 as dept_id, 0 as parent_id, '0' as ancestors, '大富翁金融公司' as dept_name, 10 as order_num, '总经理办公室' as leader, '13800000001' as phone, 'gm@dfw.local' as email
  union all select 210, 200, '0,200', '第一战区', 1, '战区总监A', '13800000011', 'zone1@dfw.local'
  union all select 211, 210, '0,200,210', '第一战区-销售一部', 1, '部门经理A1', '13800000111', 'sale11@dfw.local'
  union all select 212, 210, '0,200,210', '第一战区-销售二部', 2, '部门经理A2', '13800000112', 'sale12@dfw.local'
  union all select 213, 210, '0,200,210', '第一战区-销售三部', 3, '部门经理A3', '13800000113', 'sale13@dfw.local'
  union all select 214, 210, '0,200,210', '第一战区-销售四部', 4, '部门经理A4', '13800000114', 'sale14@dfw.local'
  union all select 220, 200, '0,200', '第二战区', 2, '战区总监B', '13800000021', 'zone2@dfw.local'
  union all select 215, 220, '0,200,220', '第二战区-销售五部', 1, '部门经理B1', '13800000115', 'sale15@dfw.local'
  union all select 216, 220, '0,200,220', '第二战区-销售六部', 2, '部门经理B2', '13800000116', 'sale16@dfw.local'
  union all select 217, 220, '0,200,220', '第二战区-销售七部', 3, '部门经理B3', '13800000117', 'sale17@dfw.local'
  union all select 218, 220, '0,200,220', '第二战区-销售八部', 4, '部门经理B4', '13800000118', 'sale18@dfw.local'
  union all select 230, 200, '0,200', '金融部', 3, '金融经理', '13800000030', 'finance@dfw.local'
  union all select 231, 230, '0,200,230', '金融部-风控审核组', 1, '审核组长', '13800000031', 'risk@dfw.local'
  union all select 232, 230, '0,200,230', '金融部-放款运营组', 2, '放款组长', '13800000032', 'loanops@dfw.local'
  union all select 233, 230, '0,200,230', '金融部-财务会计组', 3, '会计主管', '13800000033', 'account@dfw.local'
) p
where not exists (select 1 from sys_dept d where d.dept_id = p.dept_id);

-- 组织结构标准化：删除若依科技，统一保留“大富翁金融公司”
update sys_dept
set dept_name = '大富翁金融公司',
    parent_id = 0,
    ancestors = '0',
    leader = '总经理办公室',
    update_by = 'admin',
    update_time = sysdate()
where dept_id = 200;

update sys_dept
set dept_name = '大富翁金融公司',
    parent_id = 0,
    ancestors = '0',
    leader = '总经理办公室',
    update_by = 'admin',
    update_time = sysdate()
where dept_name in ('大富翁金融事业部', '大富豪金融事业部');

update sys_dept
set ancestors = replace(ancestors, '0,100,200', '0,200'),
    update_by = 'admin',
    update_time = sysdate()
where ancestors like '0,100,200%';

update sys_dept
set parent_id = 200,
    ancestors = '0,200',
    update_by = 'admin',
    update_time = sysdate()
where dept_id in (210, 220, 230);

update sys_user
set dept_id = 211
where dept_id in (100, 101, 102, 103, 104, 105, 106, 107, 108, 109);

delete from sys_role_dept where dept_id in (100, 101, 102, 103, 104, 105, 106, 107, 108, 109);
insert into sys_role_dept(role_id, dept_id)
select 2, 200 from dual where not exists (select 1 from sys_role_dept where role_id = 2 and dept_id = 200);

delete from sys_dept where dept_id in (100, 101, 102, 103, 104, 105, 106, 107, 108, 109);

-- ----------------------------
-- 9、岗位信息（贴合贷款业务）
-- ----------------------------
insert into sys_post(post_id, post_code, post_name, post_sort, status, create_by, create_time, update_by, update_time, remark)
select p.post_id, p.post_code, p.post_name, p.post_sort, '0', 'admin', sysdate(), '', null, p.remark
from (
  select 10 as post_id, 'loan_sales_director' as post_code, '销售总监' as post_name, 10 as post_sort, '战区业绩统筹' as remark
  union all select 11, 'loan_sales_manager', '销售经理', 11, '部门客户与业绩管理'
  union all select 12, 'loan_sales_rep', '销售代表', 12, '客户开发与签约'
  union all select 13, 'loan_fin_manager', '金融经理', 13, '金融审核管理'
  union all select 14, 'loan_fin_specialist', '金融专员', 14, '合同审核与放款跟进'
  union all select 15, 'loan_accountant', '会计', 15, '服务费收款登记'
  union all select 16, 'loan_general_manager', '总经理', 16, '全局经营管理'
  union all select 17, 'loan_dept_manager', '部门经理', 17, '部门客户与人员管理'
) p
where not exists (select 1 from sys_post sp where sp.post_id = p.post_id);

update sys_post
set post_code = 'loan_general_manager',
    post_name = '总经理',
    post_sort = 16,
    status = '0',
    update_by = 'admin',
    update_time = sysdate(),
    remark = '全局经营管理'
where post_id = 16;

update sys_post
set post_code = 'loan_dept_manager',
    post_name = '部门经理',
    post_sort = 17,
    status = '0',
    update_by = 'admin',
    update_time = sysdate(),
    remark = '部门客户与人员管理'
where post_id = 17;

insert into sys_user_post(user_id, post_id)
select up.user_id, 16
from sys_user_post up
where up.post_id = 1
  and not exists (select 1 from sys_user_post t where t.user_id = up.user_id and t.post_id = 16);

insert into sys_user_post(user_id, post_id)
select up.user_id, 17
from sys_user_post up
where up.post_id in (2, 3)
  and not exists (select 1 from sys_user_post t where t.user_id = up.user_id and t.post_id = 17);

delete from sys_user_post where post_id in (1, 2, 3);
delete from sys_post where post_id in (1, 2, 3);

-- ----------------------------
-- 10、业务角色与权限
-- ----------------------------
insert into sys_role(
  role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, update_by, update_time, remark
)
select p.role_id, p.role_name, p.role_key, p.role_sort, p.data_scope, 1, 1,
       '0', '0', 'admin', sysdate(), '', null, p.remark
from (
  select 210 as role_id, '销售代表' as role_name, 'loan_sales_rep' as role_key, 10 as role_sort, '5' as data_scope, '仅本人客户与合同处理' as remark
  union all select 211, '销售经理', 'loan_sales_manager', 11, '3', '本部门客户管理与业绩统计'
  union all select 212, '销售总监', 'loan_sales_director', 12, '2', '战区维度经营分析'
  union all select 213, '金融专员', 'loan_fin_specialist', 13, '3', '合同审核与放款跟进'
  union all select 214, '金融经理', 'loan_fin_manager', 14, '4', '金融部及下级团队管理'
  union all select 215, '会计', 'loan_accountant', 15, '3', '服务费登记与核对'
  union all select 216, '总经理', 'loan_general_manager', 16, '1', '全量经营数据查看'
) p
where not exists (select 1 from sys_role r where r.role_id = p.role_id);

insert into sys_role_menu(role_id, menu_id)
select p.role_id, p.menu_id
from (
  -- 销售代表
  select 210 as role_id, 3000 as menu_id union all
  select 210, 3001 union all select 210, 3002 union all
  select 210, 3010 union all select 210, 3011 union all select 210, 3012 union all
  select 210, 3018 union all select 210, 3020 union all select 210, 3021 union all
  -- 销售经理
  select 211, 3000 union all
  select 211, 3001 union all select 211, 3002 union all
  select 211, 3010 union all select 211, 3011 union all select 211, 3012 union all
  select 211, 3014 union all select 211, 3015 union all select 211, 3016 union all
  select 211, 3018 union all select 211, 3019 union all
  select 211, 3020 union all select 211, 3021 union all select 211, 3022 union all select 211, 3024 union all
  -- 销售总监
  select 212, 3000 union all
  select 212, 3001 union all select 212, 3002 union all
  select 212, 3010 union all
  select 212, 3020 union all
  -- 金融专员
  select 213, 3000 union all
  select 213, 3001 union all select 213, 3002 union all
  select 213, 3010 union all
  select 213, 3020 union all select 213, 3025 union all select 213, 3026 union all
  -- 金融经理
  select 214, 3000 union all
  select 214, 3001 union all select 214, 3002 union all
  select 214, 3010 union all
  select 214, 3020 union all select 214, 3022 union all
  select 214, 3024 union all select 214, 3025 union all select 214, 3026 union all
  -- 会计
  select 215, 3000 union all
  select 215, 3002 union all
  select 215, 3020 union all select 215, 3026 union all
  -- 总经理
  select 216, 3000 union all
  select 216, 3001 union all select 216, 3002 union all
  select 216, 3010 union all select 216, 3011 union all select 216, 3012 union all
  select 216, 3013 union all select 216, 3014 union all select 216, 3015 union all
  select 216, 3016 union all select 216, 3017 union all select 216, 3018 union all select 216, 3019 union all
  select 216, 3020 union all select 216, 3021 union all select 216, 3022 union all select 216, 3023 union all
  select 216, 3024 union all select 216, 3025 union all select 216, 3026
) p
where exists (select 1 from sys_role r where r.role_id = p.role_id)
  and exists (select 1 from sys_menu m where m.menu_id = p.menu_id)
  and not exists (
    select 1 from sys_role_menu rm
    where rm.role_id = p.role_id and rm.menu_id = p.menu_id
  );

-- 销售总监按战区配置自定义数据范围
insert into sys_role_dept(role_id, dept_id)
select 212, p.dept_id
from (
  select 210 as dept_id union all select 220 union all
  select 211 union all select 212 union all select 213 union all select 214 union all
  select 215 union all select 216 union all select 217 union all select 218
) p
where exists (select 1 from sys_role r where r.role_id = 212)
  and exists (select 1 from sys_dept d where d.dept_id = p.dept_id)
  and not exists (
    select 1 from sys_role_dept rd
    where rd.role_id = 212 and rd.dept_id = p.dept_id
  );

-- ----------------------------
-- 11、业务示例账号（用于快速验收权限）
-- ----------------------------
insert into sys_user(
  dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
  password, status, del_flag, login_ip, login_date, pwd_update_date,
  create_by, create_time, update_by, update_time, remark
)
select p.dept_id, p.user_name, p.nick_name, '00', p.email, p.phone, '0', '',
       '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
       '0', '0', '', null, sysdate(),
       'admin', sysdate(), '', null, p.remark
from (
  select 200 as dept_id, 'loan_gm' as user_name, '贷款总经理' as nick_name, 'loan_gm@dfw.local' as email, '13790000001' as phone, '总经理账号' as remark
  union all select 210, 'loan_director_a', '销售总监A', 'director_a@dfw.local', '13790000002', '第一战区总监'
  union all select 220, 'loan_director_b', '销售总监B', 'director_b@dfw.local', '13790000003', '第二战区总监'
  union all select 211, 'loan_manager_a1', '销售经理A1', 'manager_a1@dfw.local', '13790000004', '第一战区销售一部经理'
  union all select 215, 'loan_manager_b1', '销售经理B1', 'manager_b1@dfw.local', '13790000005', '第二战区销售五部经理'
  union all select 211, 'loan_sales_a1', '销售代表A1', 'sales_a1@dfw.local', '13790000006', '销售代表'
  union all select 212, 'loan_sales_a2', '销售代表A2', 'sales_a2@dfw.local', '13790000007', '销售代表'
  union all select 215, 'loan_sales_b1', '销售代表B1', 'sales_b1@dfw.local', '13790000008', '销售代表'
  union all select 216, 'loan_sales_b2', '销售代表B2', 'sales_b2@dfw.local', '13790000009', '销售代表'
  union all select 230, 'loan_fin_mgr', '金融经理', 'fin_mgr@dfw.local', '13790000010', '金融经理'
  union all select 231, 'loan_fin_sp1', '金融专员1', 'fin_sp1@dfw.local', '13790000011', '金融审核专员'
  union all select 232, 'loan_fin_sp2', '金融专员2', 'fin_sp2@dfw.local', '13790000012', '放款跟进专员'
  union all select 233, 'loan_acc', '会计', 'loan_acc@dfw.local', '13790000013', '服务费收款会计'
) p
where not exists (select 1 from sys_user u where u.user_name = p.user_name);

insert into sys_user_role(user_id, role_id)
select u.user_id, p.role_id
from (
  select 'loan_gm' as user_name, 216 as role_id union all
  select 'loan_director_a', 212 union all
  select 'loan_director_b', 212 union all
  select 'loan_manager_a1', 211 union all
  select 'loan_manager_b1', 211 union all
  select 'loan_sales_a1', 210 union all
  select 'loan_sales_a2', 210 union all
  select 'loan_sales_b1', 210 union all
  select 'loan_sales_b2', 210 union all
  select 'loan_fin_mgr', 214 union all
  select 'loan_fin_sp1', 213 union all
  select 'loan_fin_sp2', 213 union all
  select 'loan_acc', 215
) p
inner join sys_user u on u.user_name = p.user_name
where not exists (
  select 1 from sys_user_role ur
  where ur.user_id = u.user_id and ur.role_id = p.role_id
);

insert into sys_user_post(user_id, post_id)
select u.user_id, p.post_id
from (
  select 'loan_gm' as user_name, 16 as post_id union all
  select 'loan_director_a', 10 union all
  select 'loan_director_b', 10 union all
  select 'loan_manager_a1', 11 union all
  select 'loan_manager_b1', 11 union all
  select 'loan_sales_a1', 12 union all
  select 'loan_sales_a2', 12 union all
  select 'loan_sales_b1', 12 union all
  select 'loan_sales_b2', 12 union all
  select 'loan_fin_mgr', 13 union all
  select 'loan_fin_sp1', 14 union all
  select 'loan_fin_sp2', 14 union all
  select 'loan_acc', 15
) p
inner join sys_user u on u.user_name = p.user_name
where not exists (
  select 1 from sys_user_post up
  where up.user_id = u.user_id and up.post_id = p.post_id
);

-- 业务角色授权补充：工作日志与业绩分析
insert into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
from (
  select 210 as role_id union all
  select 211 union all
  select 212 union all
  select 213 union all
  select 214 union all
  select 215 union all
  select 216
) r
cross join (
  select 3004 as menu_id union all
  select 3040 union all select 3041 union all select 3042 union all select 3043 union all select 3044
) m
where exists (select 1 from sys_role sr where sr.role_id = r.role_id)
  and exists (select 1 from sys_menu sm where sm.menu_id = m.menu_id)
  and not exists (
    select 1 from sys_role_menu rm where rm.role_id = r.role_id and rm.menu_id = m.menu_id
  );

insert into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
from (
  select 210 as role_id union all
  select 211 union all
  select 212 union all
  select 216
) r
cross join (
  select 3003 as menu_id union all
  select 3030 union all select 3031 union all select 3032 union all select 3033
) m
where exists (select 1 from sys_role sr where sr.role_id = r.role_id)
  and exists (select 1 from sys_menu sm where sm.menu_id = m.menu_id)
  and not exists (
    select 1 from sys_role_menu rm where rm.role_id = r.role_id and rm.menu_id = m.menu_id
  );

insert into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
from (
  select 213 as role_id union all
  select 214 union all
  select 215
) r
cross join (
  select 3003 as menu_id union all
  select 3030
) m
where exists (select 1 from sys_role sr where sr.role_id = r.role_id)
  and exists (select 1 from sys_menu sm where sm.menu_id = m.menu_id)
  and not exists (
    select 1 from sys_role_menu rm where rm.role_id = r.role_id and rm.menu_id = m.menu_id
  );

-- ----------------------------
-- 12、工作日志样例数据（近30天）
-- ----------------------------
insert into loan_work_log(
  work_date, user_id, dept_id, call_count, valid_call_count,
  intent_customer_count, visit_count, signed_count,
  create_by, create_time, update_by, update_time, remark
)
select date_sub(curdate(), interval s.day_no day) as work_date,
       u.user_id,
       u.dept_id,
       35 + mod(s.day_no * 7 + s.user_no * 5, 40) as call_count,
       12 + mod(s.day_no * 3 + s.user_no * 2, 22) as valid_call_count,
       4 + mod(s.day_no + s.user_no, 10) as intent_customer_count,
       2 + mod(s.day_no + s.user_no * 2, 6) as visit_count,
       mod(s.day_no + s.user_no, 4) as signed_count,
       'loan.seed', sysdate(), '', null,
       '工作日志样例'
from (
  select d.n as day_no, u.n as user_no
  from (
    select 0 as n union all select 1 union all select 2 union all select 3 union all select 4 union all
    select 5 union all select 6 union all select 7 union all select 8 union all select 9 union all
    select 10 union all select 11 union all select 12 union all select 13 union all select 14 union all
    select 15 union all select 16 union all select 17 union all select 18 union all select 19 union all
    select 20 union all select 21 union all select 22 union all select 23 union all select 24 union all
    select 25 union all select 26 union all select 27 union all select 28 union all select 29
  ) d
  cross join (
    select 1 as n union all select 2 union all select 3 union all select 4
  ) u
) s
inner join (
  select 1 as user_no, user_id, dept_id from sys_user where user_name = 'loan_sales_a1'
  union all
  select 2 as user_no, user_id, dept_id from sys_user where user_name = 'loan_sales_a2'
  union all
  select 3 as user_no, user_id, dept_id from sys_user where user_name = 'loan_sales_b1'
  union all
  select 4 as user_no, user_id, dept_id from sys_user where user_name = 'loan_sales_b2'
) u on s.user_no = u.user_no
where not exists (
  select 1 from loan_work_log lw
  where lw.user_id = u.user_id
    and lw.work_date = date_sub(curdate(), interval s.day_no day)
);

-- ----------------------------
-- 13、客户与合同样例数据（各220条，可重复执行）
-- ----------------------------
insert into loan_customer(
  customer_name, customer_type, phone, id_card_no, company_name, business_license_no,
  source_type, intent_level, intent_amount, status, user_id, dept_id,
  last_follow_time, next_follow_time, signed_time, sea_time, transfer_remark,
  create_by, create_time, update_by, update_time, remark
)
select concat('样例客户', lpad(s.n, 4, '0')) as customer_name,
       case when mod(s.n, 4) = 0 then 'C' else 'P' end as customer_type,
       concat('139', lpad(s.n, 8, '0')) as phone,
       case when mod(s.n, 4) = 0 then null else concat('4301', lpad(s.n, 14, '0')) end as id_card_no,
       case when mod(s.n, 4) = 0 then concat('大富翁合作企业', lpad(s.n, 4, '0')) else null end as company_name,
       case when mod(s.n, 4) = 0 then concat('915001', lpad(s.n, 12, '0')) else null end as business_license_no,
       case mod(s.n, 3) when 0 then 'INTRO' when 1 then 'PHONE' else 'REFERRAL' end as source_type,
       case mod(s.n, 3) when 0 then 'A' when 1 then 'B' else 'C' end as intent_level,
       cast(50000 + mod(s.n * 137, 300000) as decimal(14,2)) as intent_amount,
       case when mod(s.n, 9) = 0 then 'SEA' when mod(s.n, 5) = 0 then 'SIGNED' else 'PRIVATE' end as status,
       case mod(s.n, 4)
         when 1 then sa1.user_id
         when 2 then sa2.user_id
         when 3 then sb1.user_id
         else sb2.user_id
       end as user_id,
       case mod(s.n, 4) when 1 then 211 when 2 then 212 when 3 then 215 else 216 end as dept_id,
       date_sub(sysdate(), interval mod(s.n, 45) day) as last_follow_time,
       date_add(sysdate(), interval mod(s.n, 15) + 1 day) as next_follow_time,
       case when mod(s.n, 5) = 0 then date_sub(sysdate(), interval mod(s.n, 80) day) else null end as signed_time,
       case when mod(s.n, 9) = 0 then date_sub(sysdate(), interval mod(s.n, 20) day) else null end as sea_time,
       case when mod(s.n, 9) = 0 then '超期自动转入公海（样例）' else null end as transfer_remark,
       'loan.seed', date_sub(sysdate(), interval mod(s.n, 90) day), 'loan.seed', sysdate(),
       '贷款业务样例客户数据'
from (
  select ones.n + tens.n * 10 + hundreds.n * 100 + 1 as n
  from (
    select 0 as n union all select 1 union all select 2 union all select 3 union all select 4
    union all select 5 union all select 6 union all select 7 union all select 8 union all select 9
  ) ones
  cross join (
    select 0 as n union all select 1 union all select 2 union all select 3 union all select 4
    union all select 5 union all select 6 union all select 7 union all select 8 union all select 9
  ) tens
  cross join (
    select 0 as n union all select 1 union all select 2 union all select 3 union all select 4
    union all select 5 union all select 6 union all select 7 union all select 8 union all select 9
  ) hundreds
) s
left join (select user_id from sys_user where user_name = 'loan_sales_a1' limit 1) sa1 on 1 = 1
left join (select user_id from sys_user where user_name = 'loan_sales_a2' limit 1) sa2 on 1 = 1
left join (select user_id from sys_user where user_name = 'loan_sales_b1' limit 1) sb1 on 1 = 1
left join (select user_id from sys_user where user_name = 'loan_sales_b2' limit 1) sb2 on 1 = 1
where s.n <= 220
  and not exists (
  select 1 from loan_customer c where c.phone = concat('139', lpad(s.n, 8, '0'))
);

insert into loan_contract(
  contract_no, customer_id, user_id, dept_id, product_name, attachment_urls,
  contract_amount, disburse_amount,
  first_fee_amount, second_fee_amount, first_fee_paid, second_fee_paid,
  status, sign_time, submit_time, finance_audit_time, bank_loan_time,
  finance_user_id, bank_result,
  create_by, create_time, update_by, update_time, remark
)
select concat('LC', lpad(s.n, 6, '0')) as contract_no,
       c.customer_id,
       c.user_id,
       c.dept_id,
       case mod(s.n, 4) when 0 then '车抵贷' when 1 then '经营贷' when 2 then '税票贷' else '信用贷' end as product_name,
       'https://example.com/contract/sample.pdf' as attachment_urls,
       cast(80000 + mod(s.n * 521, 420000) as decimal(14,2)) as contract_amount,
       case
         when mod(s.n, 4) = 0 then cast((80000 + mod(s.n * 521, 420000)) * 0.95 as decimal(14,2))
         else 0.00
       end as disburse_amount,
       cast((80000 + mod(s.n * 521, 420000)) * 0.02 as decimal(14,2)) as first_fee_amount,
       cast((80000 + mod(s.n * 521, 420000)) * 0.01 as decimal(14,2)) as second_fee_amount,
       cast((80000 + mod(s.n * 521, 420000)) * 0.02 as decimal(14,2)) as first_fee_paid,
       case
         when mod(s.n, 4) = 0 then cast((80000 + mod(s.n * 521, 420000)) * 0.005 as decimal(14,2))
         else 0.00
       end as second_fee_paid,
       case
         when mod(s.n, 10) = 0 then 'REJECTED'
         when mod(s.n, 4) = 0 then 'LOANED'
         when mod(s.n, 3) = 0 then 'BANK_REVIEW'
         else 'FINANCE_REVIEW'
       end as status,
       date_sub(sysdate(), interval mod(s.n, 60) + 5 day) as sign_time,
       date_sub(sysdate(), interval mod(s.n, 60) + 4 day) as submit_time,
       date_sub(sysdate(), interval mod(s.n, 60) + 2 day) as finance_audit_time,
       case when mod(s.n, 4) = 0 then date_sub(sysdate(), interval mod(s.n, 30) day) else null end as bank_loan_time,
       case when mod(s.n, 2) = 0 then f1.user_id else f2.user_id end as finance_user_id,
       case
         when mod(s.n, 10) = 0 then '银行拒绝：征信评分不足（样例）'
         when mod(s.n, 4) = 0 then '银行放款成功（样例）'
         when mod(s.n, 3) = 0 then '银行复核中（样例）'
         else '金融审核中（样例）'
       end as bank_result,
       'loan.seed', date_sub(sysdate(), interval mod(s.n, 90) day), 'loan.seed', sysdate(),
       '贷款业务样例合同数据'
from (
  select ones.n + tens.n * 10 + hundreds.n * 100 + 1 as n
  from (
    select 0 as n union all select 1 union all select 2 union all select 3 union all select 4
    union all select 5 union all select 6 union all select 7 union all select 8 union all select 9
  ) ones
  cross join (
    select 0 as n union all select 1 union all select 2 union all select 3 union all select 4
    union all select 5 union all select 6 union all select 7 union all select 8 union all select 9
  ) tens
  cross join (
    select 0 as n union all select 1 union all select 2 union all select 3 union all select 4
    union all select 5 union all select 6 union all select 7 union all select 8 union all select 9
  ) hundreds
) s
inner join loan_customer c
  on c.phone = concat('139', lpad(s.n, 8, '0'))
left join (select user_id from sys_user where user_name = 'loan_fin_sp1' limit 1) f1 on 1 = 1
left join (select user_id from sys_user where user_name = 'loan_fin_sp2' limit 1) f2 on 1 = 1
where s.n <= 220
  and not exists (
  select 1 from loan_contract ct where ct.contract_no = concat('LC', lpad(s.n, 6, '0'))
);
