package pt.ulisboa.tecnico.cmu.locmess.session.data;

import java.util.HashSet;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.main.profile.PairModel;
import pt.ulisboa.tecnico.cmu.locmess.session.LocMessService;

public class Profile {
    private Set<PairModel> keys;

    public Profile(Set<PairModel> keys){
        this.keys = keys;
    }

    public Set<MessageModel> match(Set<MessageModel> msgs){
        Set<MessageModel> res = new HashSet<>();

        msgloop:
        for(MessageModel msg : msgs){

            if(!msg.isNow()) continue msgloop;

            Set<PairModel> msgKeys = msg.getFilter();
            MessageModel.MESSAGE_POLICY policy = msg.getPolicy();

            if(policy == MessageModel.MESSAGE_POLICY.WHITELIST)
                if(!keys.containsAll(msgKeys))
                    continue msgloop;

            if(policy == MessageModel.MESSAGE_POLICY.BLACKLIST)
                for(PairModel key : msgKeys)
                    if(keys.contains(key))
                        continue msgloop;

            res.add(msg);
        }
        return res;
    }
}
