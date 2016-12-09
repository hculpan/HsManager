package org.culpan.hsmgr;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by USUCUHA on 12/9/2016.
 */
@XmlRootElement(name = "combat")
public class Combat {
    Set<Combatant> combatants = new HashSet<>();

    @XmlElement(name = "combatant")
    public Set<Combatant> getCombatants() {
        return combatants;
    }

    public void setCombatants(Set<Combatant> combatants) {
        this.combatants = combatants;
    }
}
