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

-- 普通角色默认授权（如不需要可手动删除）
insert into sys_role_menu(role_id, menu_id)
select 2, 3000 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3000);
insert into sys_role_menu(role_id, menu_id)
select 2, 3001 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3001);
insert into sys_role_menu(role_id, menu_id)
select 2, 3002 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 3002);
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

-- ----------------------------
-- 7、移除默认“若依官网”菜单（适配本项目）
-- ----------------------------
delete from sys_role_menu where menu_id = 4;
delete from sys_menu where menu_id = 4;
