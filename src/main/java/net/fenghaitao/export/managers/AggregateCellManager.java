package net.fenghaitao.export.managers;

import lombok.Data;
import net.fenghaitao.enums.AggregateType;

@Data
public class AggregateCellManager extends BaseCellManager {
    private AggregateType aggregateType;
}