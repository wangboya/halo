#### 前进四版本

原本大量的全表查询过滤,效率太低

而实际生产也不可能用内存数据库

因此,基于要速度不要空间的考虑

进行了相关改造

将所有的extension全部缓存到了内存

写操作同时同步到数据库

测试结果如下

同机器,9128篇文章,32.9MB数据

|                                           | 前进四 | 标准版本 |
| ----------------------------------------- | ------ | -------- |
| 添加并发布文章,mysql,篇/秒                | 3.5    | 0.5      |
| 添加并发布文章,H2,篇/秒                   |        |          |
| 查询文章,仅限制20条,mysql,总数9128条,毫秒 |        |          |
|                                           |        |          |

