@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    @Override
    public Map getCorresBatchNumInfo(FindCorresBatchNumRequest request) throws BaseException {
        Map map = new HashMap<>();

        // 获取用户信息
        User user = SessionUser.getUserInfo();
        request.setUserids(user.getUserIds());
        request.setUserid(user.getId());
        request.setGroupid(user.getGroupId());

        //去除掉request中startDate和endDate字符串中的"-"
        request.setStartDate(request.getStartDate().replace("-",""));
        request.setEndDate(request.getEndDate().replace("-",""));

        List<String> list = request.getBatchNum();
        //遍历request中的批次号List，从中取出每一个批次号进行sql查询
        for (String s : list) {
            request.setCorresBatchNumStr(s.trim());
            List<Map<String, Object>> resultList = qualityCheckInfoDao.findCorresBatchNumInfo(request);
            for (Map<String, Object> infoMap : resultList) {
                String samplingDate = infoMap.get("productDate").toString();

                //拼接返回显示取样时间为"年-月-日 时：分"格式
                String samplingDateAndTime = samplingDate.substring(0, 4) + "-" +
                        samplingDate.substring(4, 6) + "-" + samplingDate.substring(6, 8) +
                        " " + infoMap.get("samplingTime").toString();
                infoMap.put("date", samplingDateAndTime);

                //删除查询结果中productDate字段和samplingTime
                Iterator<String> iter = infoMap.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    if ("productDate".equals(key) || "samplingTime".equals(key)) {
                        iter.remove();
                    }
                }

            }

            map.put(s,resultList);
        }

        return map;
    }
