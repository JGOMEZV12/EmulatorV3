package com.polar.habbohotel.groups;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class GroupManager {

    private final GroupRepository groupRepository;
    private final GroupRankRepository groupRankRepository;
    private final Map<Integer, Group> jobs = new ConcurrentHashMap<>();
    private final Map<Integer, Group> gangs = new ConcurrentHashMap<>();

    @Autowired
    public GroupManager(GroupRepository groupRepository, GroupRankRepository groupRankRepository) {
        this.groupRepository = groupRepository;
        this.groupRankRepository = groupRankRepository;
    }

    public void init() {
        log.info("Loading groups...");
        jobs.clear();
        gangs.clear();

        List<GroupEntity> groupEntities = groupRepository.findAll();
        for (GroupEntity e : groupEntities) {
            Group group = new Group(e.getId(), e.getName(), e.getDescription(), e.getBadge(), e.getRoomId(), e.getOwnerId());
            group.setColour1(e.getColour1());
            group.setColour2(e.getColour2());
            group.setAdminOnlyDeco(e.getAdminOnlyDeco());
            group.setBalance(e.getBalance());
            group.setStock(e.getStock());
            group.setHasChat(e.isHasChat());

            List<GroupRankEntity> rankEntities = groupRankRepository.findByJobId(e.getId());
            for (GroupRankEntity re : rankEntities) {
                String[] commands = re.getCommands() != null ? re.getCommands().split(",") : new String[0];
                String[] workrooms = re.getWorkrooms() != null ? re.getWorkrooms().split(",") : new String[0];
                GroupRank rank = new GroupRank(re.getJobId(), re.getRank(), re.getName(), re.getMaleFigure(), re.getFemaleFigure(), re.getPay(), commands, workrooms, re.getLimit());
                group.getRanks().put(re.getRank(), rank);
            }

            if (group.getId() < 1000) {
                jobs.put(group.getId(), group);
            } else {
                group.setGang(true);
                gangs.put(group.getId(), group);
            }
        }
        log.info("Loaded {} jobs and {} gangs.", jobs.size(), gangs.size());
    }

    public Group getGroup(int id) {
        if (id < 1000) return jobs.get(id);
        return gangs.get(id);
    }
}
