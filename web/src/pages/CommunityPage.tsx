import React, { useState } from 'react';
import {
  MessageSquare,
  Heart,
  Share2,
  Send,
  Plus,
  Sparkles,
  Tag,
  ThumbsUp,
  Image as ImageIcon,
  CheckCircle,
  Filter
} from 'lucide-react';

interface Post {
  id: string;
  author: string;
  avatar: string;
  badge: string;
  timeAgo: string;
  category: 'Showcase' | 'Help' | 'Propagation' | 'Harvest';
  title: string;
  content: string;
  plantMention?: string;
  likes: number;
  isLiked: boolean;
  comments: Array<{
    id: string;
    author: string;
    text: string;
    timeAgo: string;
  }>;
}

export const CommunityPage: React.FC = () => {
  const [posts, setPosts] = useState<Post[]>([
    {
      id: 'p1',
      author: 'Elena Rostova',
      avatar: '🌿',
      badge: 'Master Grower (Lvl 5)',
      timeAgo: '2 hours ago',
      category: 'Showcase',
      title: 'First double-fenestration leaf on my Monstera cutting!',
      content:
        'Water-propagated this cutting 4 months ago in spherical LECA balls with a diluted kelp solution. Transferred to a chunky bark/perlite/coco coir mix 3 weeks ago, and look at this huge unfurling leaf!',
      plantMention: 'Monstera Deliciosa',
      likes: 24,
      isLiked: false,
      comments: [
        {
          id: 'c1',
          author: 'David K.',
          text: 'What ratio did you use for the bark and perlite? It looks so crisp!',
          timeAgo: '1 hour ago'
        },
        {
          id: 'c2',
          author: 'Elena Rostova',
          text: '40% orchid bark, 30% coco coir, 20% perlite, 10% worm castings!',
          timeAgo: '45 mins ago'
        }
      ]
    },
    {
      id: 'p2',
      author: 'Marcus Chen',
      avatar: '🍅',
      badge: 'Balcony Farmer',
      timeAgo: '4 hours ago',
      category: 'Harvest',
      title: 'First container Roma tomato cluster of the summer season',
      content:
        'Proof that you do not need an acre of land to grow delicious heirloom tomatoes! A 5-gallon fabric pot with bottom-watering reservoir produced 18 bright red tomatoes.',
      plantMention: 'Roma Tomato',
      likes: 41,
      isLiked: true,
      comments: [
        {
          id: 'c3',
          author: 'Sarah Jenkins',
          text: 'Fabric pots are game changers for air-pruning the roots. Congrats on the harvest!',
          timeAgo: '2 hours ago'
        }
      ]
    },
    {
      id: 'p3',
      author: 'Amara Okafor',
      avatar: '🪴',
      badge: 'Urban Botanist',
      timeAgo: 'Yesterday',
      category: 'Help',
      title: 'Edges curling upward on Calathea Orbifolia? Humidity vs Water?',
      content:
        'My hygrometer reads 58% humidity, but the outer leaf margins are beginning to crisp. I only use distilled water at room temperature. Has anyone encountered this before?',
      plantMention: 'Calathea Orbifolia',
      likes: 12,
      isLiked: false,
      comments: [
        {
          id: 'c4',
          author: 'PlantPal AI Bot',
          text: 'Curling margins with adequate humidity often indicate underwatering in the root core or airflow drafts from HVAC vents.',
          timeAgo: '18 hours ago'
        }
      ]
    }
  ]);

  const [activeCategory, setActiveCategory] = useState<string>('All');
  const [newPostModal, setNewPostModal] = useState(false);
  const [newTitle, setNewTitle] = useState('');
  const [newContent, setNewContent] = useState('');
  const [newCategory, setNewCategory] = useState<'Showcase' | 'Help' | 'Propagation' | 'Harvest'>('Showcase');
  const [newPlant, setNewPlant] = useState('');
  const [activeCommentPostId, setActiveCommentPostId] = useState<string | null>(null);
  const [commentDraft, setCommentDraft] = useState('');

  const toggleLike = (postId: string) => {
    setPosts(prev =>
      prev.map(p => {
        if (p.id === postId) {
          return {
            ...p,
            isLiked: !p.isLiked,
            likes: p.isLiked ? p.likes - 1 : p.likes + 1
          };
        }
        return p;
      })
    );
  };

  const handleAddComment = (postId: string) => {
    if (!commentDraft.trim()) return;
    setPosts(prev =>
      prev.map(p => {
        if (p.id === postId) {
          return {
            ...p,
            comments: [
              ...p.comments,
              {
                id: `c-${Date.now()}`,
                author: 'You (Gardener)',
                text: commentDraft.trim(),
                timeAgo: 'Just now'
              }
            ]
          };
        }
        return p;
      })
    );
    setCommentDraft('');
  };

  const handleCreatePost = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim() || !newContent.trim()) return;
    const post: Post = {
      id: `p-${Date.now()}`,
      author: 'You (Gardener)',
      avatar: '🌱',
      badge: 'Green Thumb',
      timeAgo: 'Just now',
      category: newCategory,
      title: newTitle.trim(),
      content: newContent.trim(),
      plantMention: newPlant.trim() || undefined,
      likes: 1,
      isLiked: true,
      comments: []
    };
    setPosts([post, ...posts]);
    setNewPostModal(false);
    setNewTitle('');
    setNewContent('');
    setNewPlant('');
  };

  const filteredPosts =
    activeCategory === 'All'
      ? posts
      : posts.filter(p => p.category === activeCategory);

  return (
    <div className="max-w-5xl mx-auto px-4 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-6 rounded-3xl border border-sage-300/40 shadow-sm">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 bg-sage-100 rounded-full text-forest-700 text-xs font-bold mb-2">
            <Sparkles className="w-3.5 h-3.5 text-forest-700" />
            <span>Plant Enthusiast Community</span>
          </div>
          <h1 className="text-2xl lg:text-3xl font-extrabold text-forest-900">
            Gardeners Club & Feed
          </h1>
          <p className="text-sm text-sage-700 mt-1">
            Exchange propagation tricks, diagnose rare diseases, and celebrate vibrant leaf flushes.
          </p>
        </div>

        <button
          onClick={() => setNewPostModal(true)}
          className="px-5 py-2.5 rounded-2xl text-xs font-bold bg-forest-700 hover:bg-forest-900 text-white flex items-center gap-2 shadow-sm transition-all"
        >
          <Plus className="w-4 h-4" />
          <span>Share Garden Story</span>
        </button>
      </div>

      {/* Category Filter Pills */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1">
        {['All', 'Showcase', 'Help', 'Propagation', 'Harvest'].map((cat) => (
          <button
            key={cat}
            onClick={() => setActiveCategory(cat)}
            className={`px-4 py-2 rounded-2xl text-xs font-bold whitespace-nowrap transition-all ${
              activeCategory === cat
                ? 'bg-forest-700 text-white shadow-sm'
                : 'bg-white text-sage-700 border border-sage-200 hover:border-sage-300'
            }`}
          >
            {cat === 'All' ? '🌱 All Stories' : `#${cat}`}
          </button>
        ))}
      </div>

      {/* Posts Stream */}
      <div className="space-y-6">
        {filteredPosts.map((post) => {
          const isCommenting = activeCommentPostId === post.id;
          return (
            <article
              key={post.id}
              className="bg-white p-6 lg:p-7 rounded-3xl border border-sage-300/40 shadow-sm space-y-4 hover:border-sage-400/80 transition-all"
            >
              {/* Author & Header */}
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-11 h-11 rounded-2xl bg-forest-100 flex items-center justify-center text-xl shadow-inner">
                    {post.avatar}
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="text-sm font-bold text-forest-900">{post.author}</h3>
                      <span className="text-[11px] px-2 py-0.5 bg-sage-100 rounded-md font-semibold text-forest-700">
                        {post.badge}
                      </span>
                    </div>
                    <div className="flex items-center gap-2 text-xs text-sage-500 mt-0.5">
                      <span>{post.timeAgo}</span>
                      <span>•</span>
                      <span className="text-forest-700 font-bold">#{post.category}</span>
                      {post.plantMention && (
                        <>
                          <span>•</span>
                          <span className="text-sage-600">🌿 {post.plantMention}</span>
                        </>
                      )}
                    </div>
                  </div>
                </div>
              </div>

              {/* Title & Body */}
              <div className="space-y-2">
                <h2 className="text-base font-bold text-forest-900">{post.title}</h2>
                <p className="text-sm text-sage-800 leading-relaxed">{post.content}</p>
              </div>

              {/* Action Buttons */}
              <div className="flex items-center justify-between border-t border-sage-100 pt-3 text-xs font-bold text-sage-600">
                <div className="flex items-center gap-4">
                  <button
                    onClick={() => toggleLike(post.id)}
                    className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl transition-all ${
                      post.isLiked
                        ? 'text-rose-600 bg-rose-50'
                        : 'hover:bg-sage-100 text-sage-700'
                    }`}
                  >
                    <Heart
                      className={`w-4 h-4 ${post.isLiked ? 'fill-current text-rose-600' : ''}`}
                    />
                    <span>{post.likes} Likes</span>
                  </button>

                  <button
                    onClick={() =>
                      setActiveCommentPostId(isCommenting ? null : post.id)
                    }
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl hover:bg-sage-100 text-sage-700 transition-all"
                  >
                    <MessageSquare className="w-4 h-4" />
                    <span>{post.comments.length} Comments</span>
                  </button>
                </div>

                <button className="flex items-center gap-1 text-sage-500 hover:text-forest-800">
                  <Share2 className="w-3.5 h-3.5" />
                  <span>Share</span>
                </button>
              </div>

              {/* Comments Section */}
              {isCommenting && (
                <div className="bg-sage-50/70 p-4 rounded-2xl space-y-3 mt-3 border border-sage-200/50">
                  <div className="space-y-2.5">
                    {post.comments.length === 0 ? (
                      <p className="text-xs text-sage-500 italic py-1">
                        No replies yet. Be the first to share your thoughts!
                      </p>
                    ) : (
                      post.comments.map(c => (
                        <div
                          key={c.id}
                          className="bg-white p-3 rounded-xl border border-sage-200/60 space-y-1"
                        >
                          <div className="flex items-center justify-between">
                            <span className="text-xs font-bold text-forest-900">
                              {c.author}
                            </span>
                            <span className="text-[10px] text-sage-500">{c.timeAgo}</span>
                          </div>
                          <p className="text-xs text-sage-800">{c.text}</p>
                        </div>
                      ))
                    )}
                  </div>

                  {/* Add Comment Input */}
                  <div className="flex gap-2 pt-2">
                    <input
                      type="text"
                      placeholder="Write an encouraging reply..."
                      value={commentDraft}
                      onChange={(e) => setCommentDraft(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') handleAddComment(post.id);
                      }}
                      className="flex-1 px-3.5 py-2 rounded-xl border border-sage-300 text-xs focus:outline-none focus:ring-2 focus:ring-forest-600 bg-white"
                    />
                    <button
                      onClick={() => handleAddComment(post.id)}
                      className="px-3.5 py-2 rounded-xl bg-forest-700 hover:bg-forest-900 text-white text-xs font-bold flex items-center gap-1"
                    >
                      <Send className="w-3 h-3" />
                      <span>Reply</span>
                    </button>
                  </div>
                </div>
              )}
            </article>
          );
        })}
      </div>

      {/* New Post Modal */}
      {newPostModal && (
        <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 border border-sage-300 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-sage-200 pb-3">
              <div className="flex items-center gap-2">
                <Sparkles className="w-5 h-5 text-forest-700" />
                <h3 className="text-lg font-bold text-forest-900">Share with Gardeners</h3>
              </div>
              <button
                onClick={() => setNewPostModal(false)}
                className="text-sage-500 hover:text-forest-900 text-sm font-bold"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleCreatePost} className="space-y-4">
              <div>
                <label className="text-xs font-bold text-forest-900 block mb-1">
                  Category
                </label>
                <select
                  value={newCategory}
                  onChange={(e) => setNewCategory(e.target.value as any)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 text-sm focus:outline-none focus:ring-2 focus:ring-forest-600 bg-white"
                >
                  <option value="Showcase">🌱 Plant Showcase & Growth Progress</option>
                  <option value="Help">🩺 Troubleshooting & Diagnosis Help</option>
                  <option value="Propagation">✂️ Propagation Tips & Cuttings</option>
                  <option value="Harvest">🍅 Harvest Brags & Vegetable Garden</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-bold text-forest-900 block mb-1">
                  Post Title
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Look at this massive new leaf on my Monstera!"
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 text-sm focus:outline-none focus:ring-2 focus:ring-forest-600"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-forest-900 block mb-1">
                  Plant Name (Optional)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Fiddle Leaf Fig, Roma Tomato, Basil"
                  value={newPlant}
                  onChange={(e) => setNewPlant(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 text-sm focus:outline-none focus:ring-2 focus:ring-forest-600"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-forest-900 block mb-1">
                  Story Details & Experience
                </label>
                <textarea
                  required
                  rows={4}
                  placeholder="Share your potting mix, watering cadence, sunlight exposure, or questions for fellow gardeners..."
                  value={newContent}
                  onChange={(e) => setNewContent(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 text-sm focus:outline-none focus:ring-2 focus:ring-forest-600"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setNewPostModal(false)}
                  className="px-4 py-2 rounded-xl text-xs font-bold text-sage-700 hover:bg-sage-100"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-bold bg-forest-700 hover:bg-forest-900 text-white shadow-md transition-all"
                >
                  Publish Story (+15 XP)
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
