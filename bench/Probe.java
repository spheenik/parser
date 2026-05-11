import skadistats.clarity.model.DTClass;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.entities.Entities;
import skadistats.clarity.processor.entities.UsesEntities;

import static skadistats.clarity.processor.entities.Entities.byDtName;
import skadistats.clarity.processor.reader.OnTickStart;
import skadistats.clarity.processor.runner.Context;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;

public class Probe {
    boolean done = false;

    @UsesEntities
    @OnTickStart
    public void onTickStart(Context ctx, boolean synthetic) {
        if (done) return;
        Entity grp = ctx.getProcessor(Entities.class).stream()
                .filter(byDtName("CDOTAGamerulesProxy")).findFirst().orElse(null);
        if (grp == null) return;
        String[] names = {
            "m_pGameRules.m_iPlayerIDsInControl",
            "m_pGameRules.m_nGameState",
            "m_pGameRules.m_iActiveTeam",
            "m_pGameRules.m_BannedHeroes.0000",
            "m_pGameRules.m_fExtraTimeRemaining.0000",
        };
        System.out.println("--- CDOTAGamerulesProxy field types ---");
        for (String n : names) {
            FieldPath fp = DTClass.getFieldPathForName(grp.getDtClass(), grp.getState(), n);
            if (fp == null) { System.out.printf("%-50s  (no fieldpath)%n", n); continue; }
            Object v = grp.getPropertyForFieldPath(fp);
            System.out.printf("%-50s  java=%s  value=%s%n",
                    n, v == null ? "null" : v.getClass().getSimpleName(), v);
        }
        done = true;
    }

    public static void main(String[] args) throws Exception {
        Probe p = new Probe();
        new SimpleRunner(new MappedFileSource(args[0])).runWith(p);
    }
}
